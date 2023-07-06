// maybe auto scanning via annotations?
package io.github.clamentos.blackhole.common.config;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Servlet;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.web.server.Server;
import io.github.clamentos.blackhole.web.servlets.EchoServlet;
import io.github.clamentos.blackhole.web.servlets.TagServlet;
import io.github.clamentos.blackhole.web.servlets.UserServlet;
import io.github.clamentos.blackhole.web.session.SessionService;

import java.security.NoSuchAlgorithmException;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Static dependency injector.</p>
 * <p>Use the method {@link Container#init} after initializing the {@link ConfigurationProvider}
 * to inject the dependencies.</p>
*/
public class Container {

    public static Server web_server;
    public static Repository repository;
    public static SessionService session_service;
    public static Servlet[] servlets;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is NOT thread safe and should be called at the beginning of everything
     * after initializing the {@link ConfigurationProvider}.</b></p>
     * This method will instantiate and inject the specified objects (primarely the servlets).
     * @throws NoSuchAlgorithmException If the method fails to instantiate the {@link SessionService}.
    */
    public static void init() throws NoSuchAlgorithmException {
        
        repository = Repository.getInstance();
        session_service = SessionService.getInstance();
        servlets = new Servlet[3];
        
        servlets[0] = UserServlet.getInstance(repository, session_service);
        servlets[1] = TagServlet.getInstance(repository, session_service);
        servlets[2] = EchoServlet.getInstance();

        web_server = Server.getInstance();
    }

    //____________________________________________________________________________________________________________________________________
}
