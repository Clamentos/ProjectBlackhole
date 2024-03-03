package io.github.clamentos.blackhole.framework.implementation.utility;

///
import io.github.clamentos.blackhole.framework.implementation.logging.LogPrinter;
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.ExceptionFormatter;

///.
import java.util.Map;
import java.util.Objects;

///
/**
 * <h3>Resource Releaser Internal</h3>
 * Provides methods to release resources, such as database connections, open files or network streams.
*/
public final class ResourceReleaserInternal {

    ///
    /**
     * Releases all the provided resources.
     * @param logger : The logger used in case of failures.
     * @param class_name : The name of the calling class used for logging.
     * @param caller_method_name : The caller method name used for logging.
     * @param resources : The resources to be closed.
     * @return {@code true} if all {@code resources} were closed, {@code false} otherwise.
    */
    public static boolean release(Logger logger, String class_name, String caller_method_name, AutoCloseable... resources) {

        boolean closed_all = true;

        for(AutoCloseable resource : resources) {

            try {

                if(resource != null) resource.close();
            }

            catch(Exception exc) {

                closed_all = false;

                logger.log(

                    class_name + "." + caller_method_name + " => " +
                    ExceptionFormatter.format(

                        "Failed to close the resource " + Objects.toIdentityString(resource), exc, "Skipping this one..."
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
     * @param logger : The logger used in case of failures.
     * @param class_name : The name of the calling class used for logging.
     * @param caller_method_name : The caller method name used for logging.
     * @param resources : The resources to be closed.
     * @return {@code true} if all {@code resources} were closed, {@code false} otherwise.
    */
    public static boolean release(LogPrinter logger, String class_name, String caller_method_name, AutoCloseable... resources) {

        boolean closed_all = true;

        for(AutoCloseable resource : resources) {

            try {

                if(resource != null) resource.close();
            }

            catch(Exception exc) {

                closed_all = false;

                logger.logToFile(

                    class_name + "." + caller_method_name + " => " +
                    ExceptionFormatter.format(

                        "Failed to close the resource " + Objects.toIdentityString(resource), exc, "Skipping this one..."
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
     * @param logs : The logs map used in case of failures.
     * @param class_name : The name of the calling class used for logging.
     * @param caller_method_name : The caller method name used for logging.
     * @param resources : The resources to be closed.
     * @return {@code true} if all {@code resources} were closed, {@code false} otherwise.
    */
    public static boolean release(Map<String, LogLevels> logs, String class_name, String caller_method_name, AutoCloseable... resources) {

        boolean closed_all = true;

        for(AutoCloseable resource : resources) {

            try {

                if(resource != null) resource.close();
            }

            catch(Exception exc) {

                closed_all = false;

                logs.put(

                    class_name + "." + caller_method_name + " => " +
                    ExceptionFormatter.format(

                        "Failed to close the resource " + Objects.toIdentityString(resource), exc, "Skipping this one..."
                    ),

                    LogLevels.WARNING
                );
            }
        }

        return(closed_all);
    }

    ///
}
