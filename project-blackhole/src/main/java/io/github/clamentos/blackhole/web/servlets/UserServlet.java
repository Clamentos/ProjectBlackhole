package io.github.clamentos.blackhole.web.servlets;

//________________________________________________________________________________________________________________________________________

import at.favre.lib.crypto.bcrypt.BCrypt;

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.QueryType;
import io.github.clamentos.blackhole.persistence.QueryWrapper;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.persistence.entities.User;
import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.ResponseStatus;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * Servlet servicing requests targeted at the user resource (resource_id 1).
*/
public class UserServlet implements Servlet {

    private static volatile UserServlet INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;
    private Repository repository;
    // session service

    //____________________________________________________________________________________________________________________________________

    private UserServlet(Repository repository) {

        LOGGER = Logger.getInstance();
        this.repository = repository;
        LOGGER.log("User servlet instantiated", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Get the UserServlet instance.
     * If the instance doesn't exist, create it.
     * @return The UserServlet instance.
     */
    public static UserServlet getInstance(Repository repository) {

        UserServlet temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new UserServlet(repository);
            }

            lock.unlock();
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
     * Always 1 in this case.
    */
    @Override
    public byte matches() {

        return(1);
    }

    /**
     * {@inheritDoc}
    */
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
        ArrayList<Object> parameters = new ArrayList<>();
        User user = null;

        parameters.add(username);

        QueryWrapper query = new QueryWrapper(
            
            QueryType.SELECT,
            "SELECT * FROM Users WHERE username = ?",
            parameters
        );

        repository.execute(query);

        while(true) {

            if(query.getStatus() == true) {

                try {

                    while(query.getResult().next()) {

                        user = new User(

                            query.getResult().getInt("id"),
                            query.getResult().getString("username"),
                            query.getResult().getString("email"),
                            query.getResult().getString("password_hash"),
                            query.getResult().getInt("registration_date"),
                            query.getResult().getInt("last_updated")
                        );
                    }

                    if(user == null) {

                        // no such user found
                        return(new Response(ResponseStatus.ERROR, null));
                    }

                    if(BCrypt.verifyer().verify(password.toCharArray(), user.password_hash()).verified) {

                        // generate session
                        // response OK with session id
                        // TODO: this
                        LOGGER.log("User " + user.id() + " logged in", LogLevel.INFO);
                    }
                }

                catch(SQLException exc) {

                    //...
                }

                return(null); // TODO: this
            }

            if(query.getStatus() == null) {

                return(new Response(ResponseStatus.ERROR, null));
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
