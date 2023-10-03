package io.github.clamentos.blackhole.network.servlets;

import io.github.clamentos.blackhole.network.transfer.Request;
import io.github.clamentos.blackhole.network.transfer.Response;
import io.github.clamentos.blackhole.network.transfer.components.Resources;
import io.github.clamentos.blackhole.scaffolding.Servlet;

public class TypeServlet implements Servlet {
    
    private static final TypeServlet INSTANCE = new TypeServlet();

    private TypeServlet() {

        //...
    }

    public static TypeServlet getInstance() {

        return(INSTANCE);
    }

    @Override
    public Resources manages() {

        return(Resources.TYPE);
    }

    @Override
    public Response handle(Request request, int request_counter, long task_id) {

        return(null);
    }
}
