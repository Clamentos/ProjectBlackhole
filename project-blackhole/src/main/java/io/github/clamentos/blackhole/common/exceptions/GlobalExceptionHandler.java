package io.github.clamentos.blackhole.common.exceptions;

import java.lang.Thread.UncaughtExceptionHandler;

public class GlobalExceptionHandler implements UncaughtExceptionHandler {

    private static final GlobalExceptionHandler INSTANCE = new GlobalExceptionHandler();

    public static GlobalExceptionHandler getInstance() {

        return(INSTANCE);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exc) {

        //...
    }
}
