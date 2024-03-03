package io.github.clamentos.blackhole.framework.implementation.utility.exportable;

///
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.NamedLogger;

///.
import java.util.Objects;

///
/**
 * <h3>Resource Releaser</h3>
 * Provides methods to release resources, such as database connections, open files or network streams.
*/
public final class ResourceReleaser {

    ///
    /**
     * Releases all the provided resources.
     * @param logger : The logger used in case of failures.
     * @param caller_method_name : The caller method name used for logging.
     * @param resources : The resources to be closed.
     * @return {@code true} if all {@code resources} were closed, {@code false} otherwise.
    */
    public static boolean release(NamedLogger logger, String caller_method_name, AutoCloseable... resources) {

        boolean closed_all = true;

        for(AutoCloseable resource : resources) {

            try {

                if(resource != null) resource.close();
            }

            catch(Exception exc) {

                closed_all = false;

                logger.log(

                    caller_method_name + " => ",
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
