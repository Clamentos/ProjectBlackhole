package io.github.clamentos.blackhole.framework.implementation.logging.exportable;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///
/**
 * <h3>Named Logger</h3>
 * Logs messages.
 * @apiNote Use this class when asynchronous logging is desired for performance reasons.
*/
public final class NamedLogger {

    ///
    /** The logger used to log. */
    private final Logger logger;

    ///..
    /** The class name of that instantiated {@code this}. */
    private final String class_name;

    ///
    /** Instantiates a new {@link NamedLogger} object. */
    public NamedLogger() {

        logger = Logger.getInstance();
        class_name = Thread.currentThread().getStackTrace()[2].getClass().getSimpleName();
    }

    ///
    /**
     * Logs the message asynchronously, blocking up to {@link ConfigurationProvider#LOG_QUEUE_INSERT_TIMEOUT} milliseconds.
     * @param calling_method_name : The name of the calling method.
     * @param message : The message to log.
     * @param severity : The severity of the log event.
     * @throws IllegalArgumentException If either {@code message} or {@code severity} are {@code null}.
     * @apiNote If this method couldn't complete in such amount of time, it will log the message synchronously as a fallback.
    */
    public void log(String calling_method_name, String message, LogLevels severity) throws IllegalArgumentException {

        if(message == null || severity == null) {

            throw new IllegalArgumentException("NamedLogger.log -> The input arguments cannot be null");
        }

        logger.log(class_name + "." + calling_method_name + " => " + message, severity);
    }

    ///..
    /**
     * Logs the message asynchronously, blocking up to {@link ConfigurationProvider#LOG_QUEUE_INSERT_TIMEOUT} milliseconds.
     * @param message : The message to log.
     * @param severity : The severity of the log event.
     * @throws IllegalArgumentException If either {@code message} or {@code severity} are {@code null}.
     * @apiNote If this method couldn't complete in such amount of time, it will log the message synchronously as a fallback.
    */
    public void log(String message, LogLevels severity) throws IllegalArgumentException {

        if(message == null || severity == null) {

            throw new IllegalArgumentException("NamedLogger.log -> The input arguments cannot be null");
        }

        logger.log(class_name + " => " + message, severity);
    }

    ///
}
