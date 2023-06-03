package io.github.clamentos.blackhole.exceptions;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.lang.Thread.UncaughtExceptionHandler;

//________________________________________________________________________________________________________________________________________

public class GlobalExceptionHandler implements UncaughtExceptionHandler {

    private static class GlobalExceptionHandlerHolder {

        private final static GlobalExceptionHandler INSTANCE = new GlobalExceptionHandler();
    }

    //____________________________________________________________________________________________________________________________________

    private GlobalExceptionHandler() {

        super();
    }

    public static GlobalExceptionHandler getInstance() {

        return(GlobalExceptionHandlerHolder.INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        LogPrinter.printToConsole("Thread: " + t.getName() + " Id: " + t.threadId() + " threw " + e.getClass().getName() + " message: " + e.getMessage(), LogLevel.WARNING);
    }

    //____________________________________________________________________________________________________________________________________
}
