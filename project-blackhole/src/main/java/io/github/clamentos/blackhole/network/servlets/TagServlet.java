package io.github.clamentos.blackhole.network.servlets;

///
import io.github.clamentos.blackhole.exceptions.Failures;
import io.github.clamentos.blackhole.exceptions.FailuresWrapper;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.network.transfer.Request;
import io.github.clamentos.blackhole.network.transfer.Response;
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Resources;
import io.github.clamentos.blackhole.network.transfer.components.ResponseStatuses;
import io.github.clamentos.blackhole.network.transfer.components.Types;
import io.github.clamentos.blackhole.network.transfer.dtos.TagReadQuery;
import io.github.clamentos.blackhole.persistence.PersistenceException;
import io.github.clamentos.blackhole.persistence.models.TagEntity;
import io.github.clamentos.blackhole.persistence.repositories.TagRepository;
import io.github.clamentos.blackhole.scaffolding.Servlet;
import io.github.clamentos.blackhole.session.SessionService;

import java.util.ArrayList;
import java.util.List;

///
public class TagServlet implements Servlet {

    private static final TagServlet INSTANCE = new TagServlet();

    private Logger logger;
    private TagRepository repository;
    private SessionService session_service;

    ///
    private TagServlet() {

        logger = Logger.getInstance();
        repository = TagRepository.getInstance();
        session_service = SessionService.getInstance();
    }

    ///
    public static TagServlet getInstance() {

        return(INSTANCE);
    }

    ///
    @Override
    public Resources manages() {

        return(Resources.TAG);
    }

    @Override
    public Response handle(Request request, int request_counter) {
        
        switch(request.method()) {

            case CREATE: return(handleCreate(request, request_counter));
            case READ: return(handleRead(request, request_counter));
            case UPDATE: return(null);
            case DELETE: return(null);

            default: return(new Response(
                
                new FailuresWrapper(Failures.UNSUPPORTED_METHOD),
                request_counter,
                "The method " + request.method().name() +
                " is not legal for the TAG resource. Only CREATE, READ, UPDATE and DELETE are allowed"
            ));
        }
    }

    ///
    private Response handleCreate(Request request, int request_counter) {

        try {

            session_service.checkPermissions(request.session_id(), 0x00000001);

            for(DataEntry entry : request.data()) {

                List<QueryParameter> temp = new ArrayList<>();

                // The regex matches any combination of: a-z, A-Z, 0-9, -, _
                // and must be between 3 and 32 long.
                temp.add(new QueryParameter(

                    entry.entryAsString("^[a-zA-Z0-9_-]{3,31}$", false),
                    SqlTypes.STRING
                ));

                temp.add(new QueryParameter((int)System.currentTimeMillis(), SqlTypes.INT));
                query_parameters.add(temp);
            }

            repository.insert(

                "INSERT INTO tags(name, creation_date) VALUES(?, ?)",
                query_parameters
            );

            return(new Response(ResponseStatuses.OK, request_counter, null));
        }

        catch(Exception exc) {

            logger.log(

                "TagServlet.handleCreate > Could not handle the request, " +
                exc.getClass().getSimpleName() + ": " + exc.getMessage(),
                LogLevel.WARNING
            );

            switch(exc) {

                case IllegalArgumentException exc1 -> {

                    return(new Response(new FailuresWrapper(Failures.BAD_FORMATTING), request_counter, exc1.getMessage()));
                }

                case PersistenceException exc1 -> {
                    
                    return(new Response(new FailuresWrapper(exc1.getFailureCause()), request_counter, exc1.getMessage()));
                }

                case SecurityException exc1 -> {

                    return(new Response(exc1.getCause(), request_counter, exc1.getMessage()));
                }

                default -> {return(new Response(new FailuresWrapper(Failures.ERROR), request_counter, "Unexpected error"));}
            }
        }
    }

    private Response handleRead(Request request, int request_counter) {

        List<QueryParameter> query_parameters;
        TagReadQuery query_dto;
        List<TagEntity> tags;
        String query;
        boolean flag;

        try {

            session_service.checkPermissions(request.session_id(), 0x00000002);
            query_dto = TagReadQuery.newInstance(request);
            query_parameters = new ArrayList<>();
            flag = false;

            switch(query_dto.mode()) {

                case 0:
                    
                    query = "SELECT " + TagEntity.getColumnNames(query_dto.fields()) +
                    " FROM tags WHERE id IN" + repository.getPlaceholders(query_dto.by_ids().size());

                    for(Integer val : query_dto.by_ids()) {

                        query_parameters.add(new QueryParameter(val, SqlTypes.INT));
                    }
                
                break;

                case 1:
                    
                    query = "SELECT " + TagEntity.getColumnNames(query_dto.fields()) +
                    " FROM tags WHERE name IN" + repository.getPlaceholders(query_dto.by_names().size());

                    for(String val : query_dto.by_names()) {

                        query_parameters.add(new QueryParameter(val, SqlTypes.STRING));
                    }
                
                break;

                case 2:

                    query = "SELECT " + TagEntity.getColumnNames(query_dto.fields()) + " FROM tags WHERE ";

                    if(query_dto.by_name_like() != null && query_dto.by_name_like() != "") {

                        query += "name LIKE ? ";
                        query_parameters.add(new QueryParameter(
                            
                            "%" + query_dto.by_name_like() + "%",
                            SqlTypes.STRING
                        ));

                        flag = true;
                    }

                    if(query_dto.creation_date_start() != null) {

                        if(flag == true) query += "AND ";
                        query = "creation_date BETWEEN ? AND ? ";
                        query_parameters.add(new QueryParameter(
                            
                            query_dto.creation_date_start(),
                            SqlTypes.INT
                        ));

                        query_parameters.add(new QueryParameter(
                            
                            query_dto.creation_date_end(),
                            SqlTypes.INT
                        ));
                    }

                    query += "AND id > ?";
                    query_parameters.add(new QueryParameter(query_dto.start(), SqlTypes.INT));

                    if(query_dto.limit() != null) {

                        query += "AND LIMIT ?";
                        query_parameters.add(new QueryParameter(query_dto.limit(), SqlTypes.INT));
                    }

                break;

                case 3:
                
                    query = "SELECT " + TagEntity.getColumnNames(query_dto.fields()) +
                    " FROM tags WHERE id > ? LIMIT ?";
                
                break;

                case 4: query = "COUNT(id) FROM tags"; break;

                default: throw new IllegalArgumentException("Unknown query mode: " + query_dto.mode());
            }

            tags = TagEntity.newInstances(repository.select(query, query_parameters));

            return(new Response(

                ResponseStatuses.OK,
                request_counter,
                () -> {

                    List<DataEntry> entries = new ArrayList<>();

                    entries.add(new DataEntry(Types.BEGIN, null));
                    
                    for(TagEntity tag : tags) {

                        entries.addAll(tag.reduce());
                    }

                    entries.add(new DataEntry(Types.END, null));

                    return(entries);                    
                }
            ));
        }

        catch(Exception exc) {

            logger.log(
                
                "TagServlet.handleRead > Could not complete the request, " +
                exc.getClass().getSimpleName() + ": " + exc.getMessage(),
                LogLevel.ERROR
            );

            return(new Response(exc.getCause(), request_counter, exc.getMessage()));
        }
    }

    ///
}
