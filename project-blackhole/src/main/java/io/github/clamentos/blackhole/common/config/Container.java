package io.github.clamentos.blackhole.common.config;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.web.server.Server;
import io.github.clamentos.blackhole.web.servlets.Servlet;
import io.github.clamentos.blackhole.web.servlets.UserServlet;
import io.github.clamentos.blackhole.web.session.SessionService;

import java.security.NoSuchAlgorithmException;

//________________________________________________________________________________________________________________________________________

/**
 * This class is responsible for initializing and injecting some hardcoded dependencies.
*/
public class Container {

    public static Server web_server;
    public static Repository repository;
    public static SessionService session_service;
    public static Servlet[] servlets;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is NOT thread safe and should be called at the beginning.</b></p>
     * This method will instantiate the specified objects.
     * @throws NoSuchAlgorithmException if the method fails to instantiate the {@link SessionService}.
    */
    public static void init() throws NoSuchAlgorithmException {
        
        repository = Repository.getInstance();
        session_service = SessionService.getInstance();

        servlets = new Servlet[1];
        servlets[0] = UserServlet.getInstance(repository, session_service);

        web_server = Server.getInstance();
    }

    //____________________________________________________________________________________________________________________________________
}
