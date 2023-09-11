package io.github.clamentos.blackhole.network.servlets;

import io.github.clamentos.blackhole.network.transfer.Request;
import io.github.clamentos.blackhole.network.transfer.Response;
import io.github.clamentos.blackhole.network.transfer.components.Resources;
import io.github.clamentos.blackhole.scaffolding.Servlet;

public class ResourceServlet implements Servlet {
    
    private static final ResourceServlet INSTANCE = new ResourceServlet();

    private ResourceServlet() {

        //...
    }

    public static ResourceServlet getInstance() {

        return(INSTANCE);
    }

    @Override
    public Resources manages() {

        return(Resources.RESOURCE);
    }

    @Override
    public Response handle(Request request, int request_counter) {

        return(null);
    }
}
