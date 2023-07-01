package io.github.clamentos.blackhole.web.servlets;

//________________________________________________________________________________________________________________________________________

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.clamentos.blackhole.common.framework.Servlet;
import io.github.clamentos.blackhole.common.framework.Streamable;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.persistence.entities.User;
import io.github.clamentos.blackhole.persistence.query.QueryType;
import io.github.clamentos.blackhole.persistence.query.QueryWrapper;
import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.UserDetails;
import io.github.clamentos.blackhole.web.dtos.components.Entities;
import io.github.clamentos.blackhole.web.dtos.components.ResponseStatus;
import io.github.clamentos.blackhole.web.dtos.queries.UserLogin;
import io.github.clamentos.blackhole.web.session.SessionService;
import io.github.clamentos.blackhole.web.session.UserSession;

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
    private SessionService session_service;

    //____________________________________________________________________________________________________________________________________

    private UserServlet(Repository repository, SessionService session_service) {

        LOGGER = Logger.getInstance();
        this.repository = repository;
        this.session_service = session_service;
        LOGGER.log("User servlet instantiated", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Get the UserServlet instance.
     * If the instance doesn't exist, create it.
     * @return The UserServlet instance.
    */
    public static UserServlet getInstance(Repository repository, SessionService session_service) {

        UserServlet temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new UserServlet(repository, session_service);
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
    public Entities matches() {

        return(Entities.USER);
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public Response handle(Request request) {

        switch(request.getMethod()) {

            case CREATE: return(null);
            case READ: return(null);
            case UPDATE: return(null);
            case DELETE: return(null);
            case LOGIN: return(login(request));

            default: return(new Response(ResponseStatus.METHOD_NOT_ALLOWED, null));
        }
    }

    //____________________________________________________________________________________________________________________________________

    private Response login(Request request) {

        User user;
        UserLogin login_info;
        QueryWrapper fetch_user;
        QueryWrapper fetch_permissions;
        UserSession session;
        UserDetails details;
        ArrayList<Streamable> result;

        login_info = UserLogin.deserialize(request.getData());
        fetch_user = new QueryWrapper(

            QueryType.SELECT,
            "SELECT U.id, U.password_hash, U.post_permission FROM Users U WHERE U.username = ?",
            login_info.username()
        );

        repository.execute(fetch_user, true);

        try {

            if(fetch_user.getStatus() == true) {

                user = User.mapSingle(null, 0b0010001001);

                if(user != null) {

                    if(BCrypt.verifyer().verify(login_info.password().toCharArray(), user.password_hash()).verified) {

                        fetch_permissions = new QueryWrapper(

                            QueryType.SELECT,
                            "SELECT UR.resource_id, UR.flags, UU.target_user_id, UU.flags FROM  AllowUsersToResources UR JOIN AllowUsersToUsers UU ON UR.user_id = UU.user_id WHERE UR.user_id = ?",
                            user.id()
                        );

                        repository.execute(fetch_permissions, true);

                        if(fetch_permissions.getStatus() == true) {

                            session = UserSession.mapSingle(fetch_permissions.getResult(), user.id(), user.post_permissions());
                            byte[] session_id = session_service.insertSession(session);
                            details = new UserDetails(session_id);
                            result = new ArrayList<>();
                            result.add(details);
                            
                            return(new Response(ResponseStatus.OK, result));
                        }
                    }
                }
            }

            return(new Response(ResponseStatus.ERROR, null));
        }

        catch(SQLException exc) {

            return(new Response(ResponseStatus.ERROR, null));
        }
    }

    //____________________________________________________________________________________________________________________________________
}
