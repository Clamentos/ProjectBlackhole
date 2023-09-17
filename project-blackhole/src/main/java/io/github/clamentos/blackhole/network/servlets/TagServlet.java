package io.github.clamentos.blackhole.network.servlets;

///
import io.github.clamentos.blackhole.exceptions.Failures;
import io.github.clamentos.blackhole.exceptions.FailuresWrapper;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.network.transfer.Request;
import io.github.clamentos.blackhole.network.transfer.Response;
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Resources;
import io.github.clamentos.blackhole.network.transfer.components.ResponseStatuses;
import io.github.clamentos.blackhole.persistence.PersistenceException;
import io.github.clamentos.blackhole.persistence.models.TagEntity;
import io.github.clamentos.blackhole.persistence.repositories.TagRepository;
import io.github.clamentos.blackhole.scaffolding.Servlet;
import io.github.clamentos.blackhole.session.SessionService;

import java.util.ArrayList;
import java.util.List;

///
public class TagServlet implements Servlet {

    private static final TagServlet INSTANCE = new TagServlet();

    private Logger logger;
    private TagRepository repository;
    private SessionService session_service;

    ///
    private TagServlet() {

        logger = Logger.getInstance();
        repository = TagRepository.getInstance();
        session_service = SessionService.getInstance();
    }

    ///
    public static TagServlet getInstance() {

        return(INSTANCE);
    }

    ///
    @Override
    public Resources manages() {

        return(Resources.TAG);
    }

    @Override
    public Response handle(Request request, int request_counter) {
        
        switch(request.method()) {

            case CREATE: return(handleCreate(request, request_counter));
            case READ: return(null);
            case UPDATE: return(null);
            case DELETE: return(null);

            default: return(new Response(
                
                new FailuresWrapper(Failures.UNSUPPORTED_METHOD),
                request_counter,
                "The method " + request.method().name() +
                " is not legal for the TAG resource. Only CREATE, READ, UPDATE and DELETE are allowed"
            ));
        }
    }

    ///
    private Response handleCreate(Request request, int request_counter) {

        List<TagEntity> tags;

        try {

            session_service.checkPermissions(request.session_id(), 0x00000001);
            tags = new ArrayList<>();
            int now = (int)(System.currentTimeMillis() / 60_000);

            for(DataEntry entry : request.data()) {

                tags.add(new TagEntity(
                    
                    null,
                    entry.entryAsString("^[a-zA-Z0-9_-]{3,31}$", false),
                    now
                ));
            }

            repository.insert(tags);
            return(new Response(ResponseStatuses.OK, request_counter, null));
        }

        catch(Exception exc) {

            logger.log(

                "TagServlet.handleCreate > Could not handle the request, " +
                exc.getClass().getSimpleName() + ": " + exc.getMessage(),
                LogLevel.WARNING
            );

            switch(exc) {

                case IllegalArgumentException exc1 -> {

                    return(new Response(new FailuresWrapper(Failures.BAD_FORMATTING), request_counter, exc1.getMessage()));
                }

                case PersistenceException exc1 -> {
                    
                    return(new Response(new FailuresWrapper(exc1.getFailureCause()), request_counter, exc1.getMessage()));
                }

                case SecurityException exc1 -> {

                    return(new Response(exc1.getCause(), request_counter, exc1.getMessage()));
                }

                default -> {
                    
                    return(new Response(
                        
                        new FailuresWrapper(Failures.ERROR),
                        request_counter,
                        "Unexpected error"
                    ));
                }
            }
        }
    }

    ///
}
