package io.github.clamentos.blackhole.network.servlets;

import io.github.clamentos.blackhole.network.transfer.Request;
import io.github.clamentos.blackhole.network.transfer.Response;
import io.github.clamentos.blackhole.network.transfer.components.Resources;
import io.github.clamentos.blackhole.scaffolding.Servlet;

public class SystemServlet implements Servlet {
    
    private static final SystemServlet INSTANCE = new SystemServlet();

    private SystemServlet() {

        //...
    }

    public static SystemServlet getInstance() {

        return(INSTANCE);
    }

    @Override
    public Resources manages() {

        return(Resources.SYSTEM);
    }

    @Override
    public Response handle(Request request) {

        return(null);
    }
}
