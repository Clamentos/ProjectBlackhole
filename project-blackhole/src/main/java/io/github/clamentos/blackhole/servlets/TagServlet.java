package io.github.clamentos.blackhole.servlets;

import io.github.clamentos.blackhole.common.exceptions.Failure;
import io.github.clamentos.blackhole.common.exceptions.Failures;
import io.github.clamentos.blackhole.common.utility.Mapper;
import io.github.clamentos.blackhole.network.Servlet;
import io.github.clamentos.blackhole.network.request.Request;
import io.github.clamentos.blackhole.network.request.Response;
import io.github.clamentos.blackhole.network.request.components.DataEntry;
import io.github.clamentos.blackhole.network.request.components.Resources;
import io.github.clamentos.blackhole.network.request.components.ResponseStatuses;
import io.github.clamentos.blackhole.persistence.QueryParameter;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.persistence.SqlTypes;

import java.util.ArrayList;
import java.util.List;

public class TagServlet implements Servlet {

    private static final TagServlet INSTANCE = new TagServlet();
    private Repository repository;

    private TagServlet() {

        repository = Repository.getInstance();
    }

    public static TagServlet getInstance() {

        return(INSTANCE);
    }
    
    @Override
    public Resources manages() {

        return(Resources.TAG);
    }

    @Override
    public Response handle(Request request) throws UnsupportedOperationException {
        
        switch(request.method()) {

            case CREATE: return(handleCreate(request));
            case READ: return(null);
            case UPDATE: return(null);
            case DELETE: return(null);

            default: throw new UnsupportedOperationException("Method not allowed for the Tag resource.");
        }
    }

    private Response handleCreate(Request request) {

        try {

            List<List<QueryParameter>> query_parameters = new ArrayList<>();

            for(DataEntry entry : request.data()) {

                List<QueryParameter> temp = new ArrayList<>();

                // TODO: pattern
                temp.add(new QueryParameter(Mapper.entryAsString(entry, "...", false), SqlTypes.STRING));
                temp.add(new QueryParameter((int)System.currentTimeMillis(), SqlTypes.INT));

                query_parameters.add(temp);
            }

            repository.insert(

                "INSERT INTO tags(name, creation_date) VALUES(?, ?)",
                query_parameters
            );
        
            return(new Response(ResponseStatuses.OK, null));
        }

        catch(IllegalArgumentException exc) {

            return(new Response(new Failure(Failures.BAD_FORMATTING), exc.getMessage()));
        }
    }
}
