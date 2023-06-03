package io.github.clamentos.blackhole.exceptions;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;

import java.lang.Thread.UncaughtExceptionHandler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//________________________________________________________________________________________________________________________________________

public class GlobalExceptionHandler implements UncaughtExceptionHandler {

    private final String RESET_COLOR = "\u001B[0m";
    private DateTimeFormatter date_formatter;

    //____________________________________________________________________________________________________________________________________

    private GlobalExceptionHandler() {

        date_formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    }

    private static class GlobalExceptionHandlerHolder {

        private final static GlobalExceptionHandler INSTANCE = new GlobalExceptionHandler();
    }

    public static GlobalExceptionHandler getInstance() {

        return(GlobalExceptionHandlerHolder.INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        String prefix = "[ " + LocalDateTime.now().format(date_formatter) + " ] -------- [ ";
        String color = LogLevel.WARNING.getColor();
        String level = LogLevel.WARNING.getValue();

        System.out.println(prefix + color + level + RESET_COLOR + " ] -------- : " + color + "Thread: " + t.getName() + " Id: " + t.threadId() + " threw " + e.getClass().getName() + " message: " + e.getMessage() + RESET_COLOR);
    }

    //____________________________________________________________________________________________________________________________________
}
