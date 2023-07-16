package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.Log;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.lang.Thread.UncaughtExceptionHandler;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Eager-loaded singleton.</b></p>
 * <p>Global exception handler.</p>
 * <p>Every thread should specify this class as its default exception handler,
 * in order to handle unexpected exceptions that would otherwise cause the thread
 * to terminate.</p>
*/
public class GlobalExceptionHandler implements UncaughtExceptionHandler {

    private static final GlobalExceptionHandler INSTANCE = new GlobalExceptionHandler();

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Get the {@link GlobalExceptionHandler} instance created during class loading.
     * @return The {@link GlobalExceptionHandler} instance.
    */
    public static GlobalExceptionHandler getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * <p>Catch the uncaught exception!
     * As of now, this method will only print the exception and the stack trace to the console.</p>
     * @param thread : The thread that threw the exception.
     * @param exc : The actual exception.
    */
    @Override
    public void uncaughtException(Thread thread, Throwable exc) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        exc.printStackTrace(pw);
        String trace = sw.toString();

        LogPrinter.printToConsole(new Log(

            "Uncaught " + exc.getClass().getSimpleName() +
            " in thread " + thread.getName() +
            ": " + exc.getMessage() + " Stack trace: " + trace,
            LogLevel.ERROR
        ));
    }

    //____________________________________________________________________________________________________________________________________
}
