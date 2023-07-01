package io.github.clamentos.blackhole.web.servlets;

import io.github.clamentos.blackhole.common.framework.Servlet;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.components.Entities;
import io.github.clamentos.blackhole.web.dtos.components.ResponseStatus;

import java.util.concurrent.locks.ReentrantLock;

public class EchoServlet implements Servlet {

    private static volatile EchoServlet INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;

    private EchoServlet() {

        LOGGER = Logger.getInstance();
        LOGGER.log("Echo servlet instantiated", LogLevel.SUCCESS);
    }

    public static EchoServlet getInstance() {

        EchoServlet temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new EchoServlet();
            }

            lock.unlock();
        }

        return(temp);
    }
    
    @Override
    public Entities matches() {

        return(Entities.ECHO);
    }

    @Override
    public Response handle(Request request) {
        
        return(new Response(

            ResponseStatus.OK,
            null
        ));
    }
}
