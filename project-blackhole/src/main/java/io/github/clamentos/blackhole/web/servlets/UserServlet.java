package io.github.clamentos.blackhole.web.servlets;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;

import java.sql.PreparedStatement;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

public class UserServlet implements Servlet {

    private static volatile UserServlet INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;
    private Repository repository;

    //____________________________________________________________________________________________________________________________________

    private UserServlet() {

        LOGGER = Logger.getInstance();
        repository = Repository.getInstance();
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the UserServlet instance.
     * If the instance doesn't exist, create it.
     * @return The UserServlet instance.
     */
    public static UserServlet getInstance() {

        UserServlet temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new UserServlet();
            }

            lock.unlock();
        }

        return(temp);
    }

    @Override
    public byte matches() {

        return(1);
    }

    @Override
    public Response handle(Request request) {

        switch(request.method()) {

            case CREATE: return(null);
            case READ: return(null);
            case UPDATE: return(null);
            case DELETE: return(null);
            case LOGIN: return(login(request));

            default: return(null);
        }
    }

    //____________________________________________________________________________________________________________________________________

    // creates new user session
    private Response login(Request request) {

        String username = new String(request.data_entries().get(0).data());
        String password = new String(request.data_entries().get(1).data());
        
        // TODO: this
        return(null);
    }

    //____________________________________________________________________________________________________________________________________
}
