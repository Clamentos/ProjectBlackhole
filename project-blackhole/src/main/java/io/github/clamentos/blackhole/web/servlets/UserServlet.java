package io.github.clamentos.blackhole.web.servlets;

//________________________________________________________________________________________________________________________________________

import at.favre.lib.crypto.bcrypt.BCrypt;

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.EntityMapper;
import io.github.clamentos.blackhole.persistence.QueryType;
import io.github.clamentos.blackhole.persistence.QueryWrapper;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.persistence.entities.EndpointPermission;
import io.github.clamentos.blackhole.persistence.entities.User;
import io.github.clamentos.blackhole.web.dtos.DtoParser;
import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.ResponseStatus;
import io.github.clamentos.blackhole.web.session.SessionService;

import java.sql.SQLException;

import java.util.List;
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

            default: return(new Response(ResponseStatus.METHOD_NOT_ALLOWED, null));
        }
    }

    //____________________________________________________________________________________________________________________________________

    private Response login(Request request) {

        User user;
        String username = new String(request.data_entries().get(0).data());
        String password = new String(request.data_entries().get(1).data());
        List<EndpointPermission> perms;
        QueryWrapper fetch_permissions;

        QueryWrapper fetch_user = new QueryWrapper(

            QueryType.SELECT,
            "SELECT * FROM Users WHERE username = ?",
            username
        );

        repository.execute(fetch_user, true);

        try {

            if(fetch_user.getStatus() == true) {

                user = EntityMapper.resultToUser(fetch_user.getResult(), 0x0000003F);

                if(user != null) {

                    if(BCrypt.verifyer().verify(password.toCharArray(), user.password_hash()).verified) {

                        fetch_permissions = new QueryWrapper(

                            QueryType.SELECT,
                            "SELECT * FROM EndpointPermissions WHERE user_id = ?",
                            user.id()
                        );

                        repository.execute(fetch_permissions, true);

                        if(fetch_permissions.getStatus() == true) {

                            perms = EntityMapper.resultToEndpointPermissions(fetch_permissions.getResult(), 0x0000000F);
                            return(DtoParser.respondRaw(ResponseStatus.OK, session_service.insertSession(perms)));
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
