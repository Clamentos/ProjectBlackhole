package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.lang.Thread.UncaughtExceptionHandler;

//________________________________________________________________________________________________________________________________________

/**
 * <ul>
 *     <li><b>BEHAVIOUR: Eager-loaded singleton.</b></li>
 *     <li><b>STEREOTYPE: Error handling.</b></li>
 * </ul>
 * <p>Global exception handler.</p>
 * Every thread should specify this class as its default exception handler,
 * in order to handle unexpected exceptions that would otherwise cause the thread
 * to terminate.
*/
public final class GlobalExceptionHandler implements UncaughtExceptionHandler {

    private static final GlobalExceptionHandler INSTANCE = new GlobalExceptionHandler();
    private LogPrinter log_printer;

    //____________________________________________________________________________________________________________________________________

    // Thread safe.
    private GlobalExceptionHandler() {

        log_printer = LogPrinter.getInstance();
        log_printer.log("GlobalExceptionHandler.new > Instantiated successfully", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * @return The {@link GlobalExceptionHandler} instance created during class loading.
    */
    public static GlobalExceptionHandler getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * <p>Catches the uncaught exception!
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

        log_printer.log(

            "Uncaught " + exc.getClass().getSimpleName() + " in thread " + thread.getName() +
            ": " + exc.getMessage() + " Stack trace: " + trace,
            LogLevel.ERROR
        );
    }

    //____________________________________________________________________________________________________________________________________
}
