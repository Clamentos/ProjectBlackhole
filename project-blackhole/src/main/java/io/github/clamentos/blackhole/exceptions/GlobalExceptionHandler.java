package io.github.clamentos.blackhole.exceptions;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.lang.Thread.UncaughtExceptionHandler;

import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

public class GlobalExceptionHandler implements UncaughtExceptionHandler {

    private static volatile GlobalExceptionHandler INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    //____________________________________________________________________________________________________________________________________

    private GlobalExceptionHandler() {

        super();
    }

    /**
     * Get the GlobalExceptionHandler instance.
     * If the instance doesn't exist, create it with default values.
     * See {@link ConfigurationProvider} for more information.
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

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        // TODO: ...
        // NOTE: temporary for now...
        LogPrinter.printToConsole("Thread: " + t.getName() + " Id: " + t.threadId() + " threw " + e.getClass().getName() + " message: " + e.getMessage(), LogLevel.ERROR);
    }

    //____________________________________________________________________________________________________________________________________
}
