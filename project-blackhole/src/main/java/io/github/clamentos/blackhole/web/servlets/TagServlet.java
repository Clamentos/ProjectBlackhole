package io.github.clamentos.blackhole.web.servlets;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Servlet;
import io.github.clamentos.blackhole.common.framework.Reducible;
import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.persistence.entities.Tag;
import io.github.clamentos.blackhole.persistence.query.QueryType;
import io.github.clamentos.blackhole.persistence.query.QueryWrapper;
import io.github.clamentos.blackhole.web.dtos.ErrorDetails;
import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.actions.TagRead;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Entities;
import io.github.clamentos.blackhole.web.dtos.components.ResponseStatus;
import io.github.clamentos.blackhole.web.session.SessionService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>This class is a singleton.</b></p>
 * This class is the {@link Servlet} to manage the request for the tags.
*/
public class TagServlet implements Servlet {
    
    private static volatile TagServlet INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;

    private Repository repository;
    private SessionService session_service;

    //____________________________________________________________________________________________________________________________________

    private TagServlet(Repository repository, SessionService session_service) {

        LOGGER = Logger.getInstance();
        this.repository = repository;
        this.session_service = session_service;
        LOGGER.log("Tag servlet instantiated", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Get the TagServlet instance.
     * If the instance doesn't exist, create it.
     * @return The TagServlet instance.
    */
    public static TagServlet getInstance(Repository repository, SessionService session_service) {

        TagServlet temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new TagServlet(repository, session_service);
            }

            lock.unlock();
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
     * Always {@code Resources.TAG} in this case.
    */
    @Override
    public Entities matches() {

        return(Entities.TAG);
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public Response handle(Request request) {

        List<Reducible> error_details;

        switch(request.method()) {

            case CREATE: return(createOrUpdate(request, false));
            case READ: return(read(request));
            case UPDATE: return(createOrUpdate(request, true));
            case DELETE: return(delete(request));

            default:

                error_details = new ArrayList<>();
                error_details.add(new ErrorDetails("Method received: " + request.method()));

            return(new Response(ResponseStatus.METHOD_NOT_ALLOWED, error_details));
        }
    }

    //________________________________________________________________________________________________________________________________________

    private Response createOrUpdate(Request request, boolean update) {

        List<Tag> tags;
        List<List<Object>> parameters;
        QueryWrapper insert;
        int now;

        parameters = new ArrayList<>();
        now = (int)(System.currentTimeMillis() / 60_000);

        try {

            session_service.checkSessionTag(request.session_id(), request.method());
            tags = Tag.deserialize(request.data());
        }

        catch(SecurityException | IllegalArgumentException exc) {

            LOGGER.log("TagServlet.createOrUpdate > Request failed, " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.WARNING);
            return(Response.create(exc.getMessage(), exc.getCause()));
        }

        if(update == false) {

            for(int i = 0; i < tags.size(); i++) {

                parameters.add(new ArrayList<>());
                parameters.get(i).add(tags.get(i).name());
                parameters.get(i).add(now);
            }

            insert = new QueryWrapper(

                QueryType.INSERT,
                "INSERT INTO Tags(name, creation_date) VALUES(?, ?)",
                parameters
            );
        }

        else {

            for(int i = 0; i < tags.size(); i++) {

                parameters.add(new ArrayList<>());
                parameters.get(i).add(tags.get(i).name());
                parameters.get(i).add(tags.get(i).id());
            }

            insert = new QueryWrapper(

                QueryType.UPDATE,
                "UPDATE Tags SET name = ? WHERE id = ?",
                parameters
            );
        }

        repository.execute(insert, true);

        if(insert.getStatus() == 1) {

            return(new Response(ResponseStatus.OK, null));
        }

        LOGGER.log("TagServlet.createOrUpdate > Request failed, " + insert.getException().getClass().getSimpleName() + ": " + insert.getException().getMessage(), LogLevel.WARNING);

        return(Response.create(
            
            insert.getException().getMessage(),
            insert.getException().getCause()
        ));
    }

    private Response read(Request request) {

        TagRead read;
        List<List<Object>> params = new ArrayList<>();
        ArrayList<String> columns = new ArrayList<>();
        List<Reducible> result;
        QueryWrapper select;
        String query;

        try {

            session_service.checkSessionTag(request.session_id(), request.method());
            read = TagRead.deserialize(request.data());

            if((read.fields() & 0b0001) > 0) columns.add("id");
            if((read.fields() & 0b0010) > 0) columns.add("name");
            if((read.fields() & 0b0100) > 0) columns.add("creation_date");

            query = columns.toString();
            query = query.substring(1, query.length() - 2);
            query = "SELECT " + query + " FROM Tags WHERE ";

            if(read.query_mode() == 0) {

                query += "id IN (";
                params.add(new ArrayList<>());

                for(int i = 0; i < read.ids().length; i++) {

                    query += "?,";
                    params.get(0).add(read.ids()[i]);
                }

                query = query.substring(0, query.length() - 2);
                query += ")";
            }

            else {

                boolean concat = false;
                params.add(new ArrayList<>());

                if(read.name_like() != null && read.name_like() != "") {

                    query += "name LIKE %?%";
                    params.get(0).add(read.name_like());
                    concat = true;
                }

                if(read.start_date() != null) {

                    if(concat == true) query += " AND ";
                    query += "creation_date >= ?";
                    params.get(0).add(read.start_date());
                    concat = true;
                }

                if(read.end_date() != null) {

                    if(concat == true) query += " AND ";
                    query += "creation_date <= ?";
                    params.get(0).add(read.end_date());
                }
            }

            select = new QueryWrapper(

                QueryType.SELECT,
                query,
                params
            );

            repository.execute(select, true);

            if(select.getStatus() == 1) {

                result = Tag.mapMany(select.getResult(), read.fields());
                return(new Response(ResponseStatus.OK, result));
            }

            LOGGER.log("TagServlet.read > Request failed, " + select.getException().getClass().getSimpleName() + ": " + select.getException().getMessage(), LogLevel.WARNING);

            return(Response.create(
                
                select.getException().getMessage(),
                select.getException().getCause()
            ));
        }

        catch(Exception exc) {

            LOGGER.log("TagServlet.createOrUpdate > Request failed, " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.WARNING);
            return(Response.create(exc.getMessage(), exc.getCause()));
        }
    }
    
    private Response delete(Request request) {

        List<List<Object>> ids;
        QueryWrapper delete;

        try {

            ids = new ArrayList<>();

            for(DataEntry entry : request.data()) {

                ids.get(0).add(Converter.entryToInt(entry));
            }

            delete = new QueryWrapper(

                QueryType.DELETE,
                "DELETE FROM Tags WHERE id = ?",
                ids
            );

            repository.execute(delete, true);

            if(delete.getStatus() == 1) {

                return(new Response(ResponseStatus.OK, null));
            }

            LOGGER.log("TagServlet.delete > Request failed, " + delete.getException().getClass().getSimpleName() + ": " + delete.getException().getMessage(), LogLevel.WARNING);

            return(Response.create(
                
                delete.getException().getMessage(),
                delete.getException().getCause()
            ));
        }

        catch(Exception exc) {

            LOGGER.log("TagServlet.delete > Request failed, " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.WARNING);
            return(Response.create(exc.getMessage(), exc.getCause()));
        }
    }

    //________________________________________________________________________________________________________________________________________
}
