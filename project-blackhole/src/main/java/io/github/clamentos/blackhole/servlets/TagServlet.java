package io.github.clamentos.blackhole.servlets;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.exceptions.Failure;
import io.github.clamentos.blackhole.common.exceptions.Failures;
import io.github.clamentos.blackhole.common.utility.Mapper;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.network.Servlet;
import io.github.clamentos.blackhole.network.request.Request;
import io.github.clamentos.blackhole.network.request.Response;
import io.github.clamentos.blackhole.network.request.components.DataEntry;
import io.github.clamentos.blackhole.network.request.components.Resources;
import io.github.clamentos.blackhole.persistence.PersistenceException;
import io.github.clamentos.blackhole.persistence.QueryParameter;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.persistence.SqlTypes;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

public class TagServlet implements Servlet {

    private static final TagServlet INSTANCE = new TagServlet();

    private Logger logger;
    private Repository repository;

    //____________________________________________________________________________________________________________________________________

    private TagServlet() {

        logger = Logger.getInstance();
        repository = Repository.getInstance();
    }

    //____________________________________________________________________________________________________________________________________

    public static TagServlet getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________
    
    @Override
    public Resources manages() {

        return(Resources.TAG);
    }

    @Override
    public Response handle(Request request) {
        
        switch(request.method()) {

            case CREATE: return(handleCreate(request));
            case READ: return(null);
            case UPDATE: return(null);
            case DELETE: return(null);

            default: return(new Response(
                
                new Failure(Failures.UNSUPPORTED_METHOD),
                "The method " + request.method().name() +
                " is not legal for the TAG resource. Only CREATE, READ, UPDATE and DELETE are allowed"
            ));
        }
    }

    //____________________________________________________________________________________________________________________________________

    private Response handleCreate(Request request) {

        // Check session...

        try {

            List<List<QueryParameter>> query_parameters = new ArrayList<>();

            for(DataEntry entry : request.data()) {

                List<QueryParameter> temp = new ArrayList<>();

                // The regex matches any combination of: a-z, A-Z, 0-9, -, _ and must be between 3 and 32 long.
                temp.add(new QueryParameter(Mapper.entryAsString(entry, "^[a-zA-Z0-9_-]{3,31}$", false), SqlTypes.STRING));
                temp.add(new QueryParameter((int)System.currentTimeMillis(), SqlTypes.INT));

                query_parameters.add(temp);
            }

            repository.insert(

                "INSERT INTO tags(name, creation_date) VALUES(?, ?)",
                query_parameters
            );
        
            return(new Response(null));
        }

        catch(IllegalArgumentException | PersistenceException exc) {

            logger.log(

                "TagServlet.handleCreate > Could not handle the request, " + exc.getClass().getSimpleName() +
                ": " + exc.getMessage(),
                LogLevel.WARNING
            );

            switch(exc) {

                case IllegalArgumentException exc1 -> {return(new Response(new Failure(Failures.BAD_FORMATTING), exc1.getMessage()));}
                case PersistenceException exc1 -> {return(new Response((Failure)exc1.getCause(), exc1.getGenericMessage()));}
                default -> {return(new Response(new Failure(Failures.ERROR), "Unexpected error"));}
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
