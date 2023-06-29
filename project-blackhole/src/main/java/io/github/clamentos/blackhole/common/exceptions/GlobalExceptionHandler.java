package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.lang.Thread.UncaughtExceptionHandler;

import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>This class is a singleton.</b></p>
 * <p>Global exception handler.</p>
 * <p>Every thread should specify this class as its default exception handler,
 * in order to handle unexpected exceptions that would otherwise cause the thread
 * to terminate.</p>
*/
public class GlobalExceptionHandler implements UncaughtExceptionHandler {

    private static volatile GlobalExceptionHandler INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * <p>Get the GlobalExceptionHandler instance.
     * If the instance doesn't exist, create it.</p>
     * @return The GlobalExceptionHandler instance.
    */
    public static GlobalExceptionHandler getInstance() {

        GlobalExceptionHandler temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new GlobalExceptionHandler();
            }

            lock.unlock();
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p>Catch the uncaught exception!
     * As of now, this method will only print the exception to the console.</p>
     * @param thread : The thread that threw the exception.
     * @param exc : The actual exception.
    */
    @Override
    public void uncaughtException(Thread thread, Throwable exc) {

        LogPrinter.printToConsole("Uncaught " + exc.getClass().getSimpleName() + " in thread " + thread.getName() + ": " + exc.getMessage() + " Stack trace: " + exc.getStackTrace().toString(), LogLevel.ERROR);
    }

    //____________________________________________________________________________________________________________________________________
}
