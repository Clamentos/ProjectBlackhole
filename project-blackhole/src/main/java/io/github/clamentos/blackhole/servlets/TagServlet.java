package io.github.clamentos.blackhole.servlets;

import java.util.ArrayList;
import java.util.List;

import io.github.clamentos.blackhole.common.util.Mapper;
import io.github.clamentos.blackhole.framework.web.Servlet;
import io.github.clamentos.blackhole.framework.web.request.Request;
import io.github.clamentos.blackhole.framework.web.request.Response;
import io.github.clamentos.blackhole.framework.web.request.components.DataEntry;
import io.github.clamentos.blackhole.framework.web.request.components.Resources;
import io.github.clamentos.blackhole.framework.web.request.components.ResponseStatuses;

public class TagServlet implements Servlet {
    
    @Override
    public Resources manages() {

        return(Resources.TAG);
    }

    @Override
    public Response handle(Request request) {

        //...
        return(null);
    }

    private Response handleCreate(Request request) {

        List<String> tag_names;

        tag_names = new ArrayList<>();

        for(DataEntry entry : request.data()) {

            tag_names.add(Mapper.entryAsString(entry, "...", false));    // TODO: pattern
        }

        // save to db
        // ...
    
        return(new Response(ResponseStatuses.OK, null));
    }
}
