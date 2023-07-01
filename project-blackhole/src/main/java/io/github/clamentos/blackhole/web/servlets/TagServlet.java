package io.github.clamentos.blackhole.web.servlets;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.framework.Servlet;
import io.github.clamentos.blackhole.common.framework.Streamable;
import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.persistence.entities.Tag;
import io.github.clamentos.blackhole.persistence.query.QueryType;
import io.github.clamentos.blackhole.persistence.query.QueryWrapper;
import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.components.Method;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Entities;
import io.github.clamentos.blackhole.web.dtos.components.ResponseStatus;
import io.github.clamentos.blackhole.web.dtos.queries.TagRead;
import io.github.clamentos.blackhole.web.session.SessionService;
import io.github.clamentos.blackhole.web.session.UserSession;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

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

        switch(request.getMethod()) {

            case CREATE: return(createOrUpdate(request, false));
            case READ: return(read(request));
            case UPDATE: return(createOrUpdate(request, true));
            case DELETE: return(delete(request));

            default: return(new Response(ResponseStatus.METHOD_NOT_ALLOWED, null));
        }
    }

    //________________________________________________________________________________________________________________________________________

    private Response createOrUpdate(Request request, boolean update) {

        List<Tag> tags;
        ArrayList<Object> parameters;
        QueryWrapper insert;
        int now;

        parameters = new ArrayList<>();
        checkSession(request.getSessionId(), request.getMethod());
        now = (int)(System.currentTimeMillis() / 60_000);

        if(update == false) {

            tags = Tag.deserialize(request.getData(), 0b0010);

            for(Tag tag : tags) {

                parameters.add(tag.name());
                parameters.add(now);
            }

            insert = new QueryWrapper(

                QueryType.INSERT,
                "INSERT INTO Tags(name, creation_date) VALUES(?, ?)",
                parameters
            );
        }

        else {

            tags = Tag.deserialize(request.getData(), 0b0011);
            
            for(Tag tag : tags) {

                parameters.add(tag.name());
                parameters.add(tag.id());
            }

            insert = new QueryWrapper(

                QueryType.UPDATE,
                "UPDATE Tags SET name = ? WHERE id = ?",
                parameters
            );
        }

        repository.execute(insert, true);

        if(insert.getStatus() == true) {

            return(new Response(ResponseStatus.OK, null));
        }

        LOGGER.log("TagServlet::createOrUpdate request failed", LogLevel.WARNING);
        return(new Response(ResponseStatus.ERROR, null));
    }

    private Response read(Request request) {

        TagRead read = TagRead.deserialize(request.getData());
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<String> columns = new ArrayList<>();
        List<Streamable> result;
        QueryWrapper select;
        String query;

        if((read.fields() & 0b0001) > 0) columns.add("id");
        if((read.fields() & 0b0010) > 0) columns.add("name");
        if((read.fields() & 0b0100) > 0) columns.add("creation_date");

        query = columns.toString();
        query = query.substring(1, query.length() - 2);
        query = "SELECT " + query + " FROM Tags WHERE ";

        if(read.query_mode() == 0) {

            query += "id IN (";

            for(int i = 0; i < read.ids().length; i++) {

                query += "?,";
                params.add(read.ids()[i]);
            }

            query = query.substring(0, query.length() - 2);
            query += ")";
        }

        else {

            boolean concat = false;

            if(read.name_like() != null && read.name_like() != "") {

                query += "name LIKE %?%";
                params.add(read.name_like());
                concat = true;
            }

            if(read.start_date() != null) {

                if(concat == true) query += " AND ";
                query += "creation_date >= ?";
                params.add(read.start_date());
                concat = true;
            }

            if(read.end_date() != null) {

                if(concat == true) query += " AND ";
                query += "creation_date <= ?";
                params.add(read.end_date());
            }
        }

        select = new QueryWrapper(

            QueryType.SELECT,
            query,
            params
        );

        repository.execute(select, true);

        if(select.getStatus() == true) {

            try {

                result = Tag.mapMany(select.getResult(), read.fields());

                return(new Response(
                
                    ResponseStatus.OK,
                    result
                ));
            }

            catch(SQLException exc) {

                return(new Response(ResponseStatus.ERROR, null));
            }
        }

        return(new Response(ResponseStatus.ERROR, null));
    }
    
    private Response delete(Request request) {

        List<Integer> ids = new ArrayList<>();
        QueryWrapper delete;

        for(DataEntry entry : request.getData()) {

            ids.add(Converter.entryToInt(entry));
        }

        delete = new QueryWrapper(

            QueryType.DELETE,
            "DELETE FROM Tags WHERE id = ?",
            ids
        );

        repository.execute(delete, true);

        if(delete.getStatus() == true) {

            return(new Response(ResponseStatus.OK, null));
        }

        return(new Response(ResponseStatus.ERROR, null));
    }

    private void checkSession(byte[] session_id, Method method) throws SecurityException {

        UserSession session;
        boolean check;
        
        switch(method) {

            case CREATE: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_CREATE; break;
            case READ: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_READ; break;
            case UPDATE: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_UPDATE; break;
            case DELETE: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_DELETE; break;

            default: check = false; break;
        }

        if(check == true) {

            session = session_service.findSession(session_id);

            if(session == null) {

                throw new SecurityException("No session found");
            }

            if(session.valid_to() < System.currentTimeMillis()) {

                session_service.removeSession(session_id);
                throw new SecurityException("Expired session");
            }

            if((session.post_permissions() & 0b0100) == 0) {

                throw new SecurityException("Not enough privileges");
            }
        }
    }

    //________________________________________________________________________________________________________________________________________
}
