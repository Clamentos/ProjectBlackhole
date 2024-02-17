package io.github.clamentos.blackhole.framework.implementation.utility;

///
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.LogPrinter;
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///.
import java.util.Objects;

///
/**
 * <h3>Resource Releaser</h3>
 * Provides static methods to release resources, such as database connections, open files or network streams.
*/
public final class ResourceReleaser {

    ///
    /**
     * Releases all the provided resources.
     * @param logger : The {@code Logger} used in case of failures.
     * @param from : The caller method name (used for logging).
     * @param resources : The variable number of resources to be closed.
     * @return {@code true} if all {@code resources} were closed, {@code false} otherwise.
     * @see Logger
    */
    public static boolean release(Logger logger, String from, AutoCloseable... resources) {

        boolean closed_all = true;

        for(AutoCloseable resource : resources) {

            try {

                if(resource != null) resource.close();
            }

            catch(Exception exc) {

                closed_all = false;

                logger.log(

                    ExceptionFormatter.format(

                        "ResourceReleaser.release >> from: " + from + " >>", exc,
                        ">> Failed in closing the resource " +
                        Objects.toIdentityString(resource)
                    ),

                    LogLevels.WARNING
                );
            }
        }

        return(closed_all);
    }

    ///..
    /**
     * Releases all the provided resources.
     * @param log_printer : The {@code LogPrinter} used in case of failures.
     * @param from : The caller method name (used for logging).
     * @param resources : Variable number of resources to be closed.
     * @return {@code true} if all {@code resources} were closed, {@code false} otherwise.
     * @see LogPrinter
    */
    public static boolean release(LogPrinter log_printer, String from, AutoCloseable... resources) {

        boolean closed_all = true;

        for(AutoCloseable resource : resources) {

            try {

                if(resource != null) resource.close();
            }

            catch(Exception exc) {

                closed_all = false;

                log_printer.logToFile(

                    ExceptionFormatter.format(

                        "ResourceReleaser.release >> from: " + from + ">> ", exc,
                        ">> Failed in closing the resource " +
                        Objects.toIdentityString(resource)
                    ),

                    LogLevels.WARNING
                );
            }
        }

        return(closed_all);
    }

    ///
}
