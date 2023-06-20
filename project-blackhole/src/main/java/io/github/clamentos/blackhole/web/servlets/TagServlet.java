package io.github.clamentos.blackhole.web.servlets;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.EntityMapper;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.persistence.query.QueryType;
import io.github.clamentos.blackhole.persistence.query.QueryWrapper;
import io.github.clamentos.blackhole.web.dtos.DataEntry;
import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.ResponseStatus;
import io.github.clamentos.blackhole.web.session.SessionService;
import io.github.clamentos.blackhole.web.session.UserSession;

import java.util.ArrayList;
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
     * Always 2 in this case.
    */
    @Override
    public byte matches() {

        return(2);
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public Response handle(Request request) {

        switch(request.method()) {

            case CREATE: create(null);
            case READ: return(null);
            case UPDATE: return(null);
            case DELETE: return(null);

            default: return(new Response(ResponseStatus.METHOD_NOT_ALLOWED, null));
        }
    }

    //________________________________________________________________________________________________________________________________________

    // Expected data entries: a list of strings for the tag names
    private Response create(Request request) {

        ArrayList<Object> parameters = new ArrayList<>();
        int now;

        try {

            checkSession(request.session_id(), 0);
        }

        catch(SecurityException exc) {

            return(new Response(ResponseStatus.UNAUTHENTICATED, null));
        }

        now = (int)(System.currentTimeMillis() / 60_000);
        
        for(DataEntry data_entry : request.data_entries()) {

            parameters.add(new String(data_entry.data()));
            parameters.add(now);
        }

        QueryWrapper insert = new QueryWrapper(

            QueryType.INSERT,
            "INSERT INTO Tags(name, creation_date) VALUES(?, ?)",
            parameters
        );

        repository.execute(insert, true);

        if(insert.getStatus() == true) {

            return(new Response(ResponseStatus.OK, null));
        }

        return(new Response(ResponseStatus.ERROR, null));
    }

    /*
     * Expected data entries:
     * 
     * 0) byte -> checklist that specifies which columns to get
     * 1) byte -> if 0: next will be list of strings
     *            if 1: next will be list of ints
     *            if 2: next will be start + end date
     *            if 3) next will be single string
    */
    private Response read(Request request) {

        int columns_to_fetch;
        String sql;
        ArrayList<Object> params;
        QueryWrapper fetch_tags;

        columns_to_fetch = request.data_entries().get(0).data()[0];
        sql = "SELECT ... FROM Tags ";
        params = new ArrayList<>();

        // query strategy
        switch(request.data_entries().get(1).data()[0]) {

            case 0: 

                sql += "WHERE Tags.name IN (";

                for(int i = 2; i < request.data_entries().size(); i++) {

                    params.add(new String(request.data_entries().get(i).data()));
                    sql += "?,";
                }

                // TODO: remove the last ','

                sql += ")";

            break;

            case 1: 

                sql += "WHERE Tags.id IN (...)";
                
                for(int i = 2; i < request.data_entries().size(); i++) {

                    params.add((int)Converter.bytesToNum(request.data_entries().get(i).data()));
                    sql += "?,";
                }

                // TODO: remove the last ','

                sql += ")";

            break;

            case 2: 

                sql += "WHERE Tags.creation_date BETWEEN ? AND ?";
                params.add((int)Converter.bytesToNum(request.data_entries().get(2).data()));
                params.add((int)Converter.bytesToNum(request.data_entries().get(3).data()));
            
            break;

            case 3: 

                sql += "WHERE Tags.name LIKE %?%";
                params.add(new String(request.data_entries().get(2).data()));
            
            break;


            default: // error
        }

        fetch_tags = new QueryWrapper(QueryType.SELECT, sql, params);
        repository.execute(fetch_tags, true);

        if(fetch_tags.getStatus() == true) {

            return(new Response(ResponseStatus.OK, EntityMapper...));
        }

        else {

            //error
        }
    }

    private void checkSession(byte[] session_id, int method) throws SecurityException {

        UserSession session;
        boolean check;
        
        switch(method) {

            case 0: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_CREATE; break;
            case 1: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_READ; break;
            case 2: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_UPDATE; break;
            case 3: check = ConfigurationProvider.NEED_SESSION_FOR_TAG_DELETE; break;

            default: check = false; break;
        }

        if(check == true) {

            session = session_service.findSession(session_id);

            if(session == null) {

                throw new SecurityException("No session found");
            }
        }
    }

    //________________________________________________________________________________________________________________________________________
}
