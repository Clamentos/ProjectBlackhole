package io.github.clamentos.blackhole.framework.implementation.utility;

///
/**
 * <h3>Exception formatter</h3>
 * Provides output formatting methods for printing exceptions.
*/
public final class ExceptionFormatter {

    ///
    /**
     * <p>Formats the given parameters into an output message.</p>
     * For example: {@code "Exception thrown: " + "IllegalArgumentException: null argument" + " Quitting..."}
     * @param pre : The message header.
     * @param exception : The target exception.
     * @param post : The message footer.
     * @return : The formatted exception message.
    */
    public static String format(String pre, Throwable exception, String post) {

        return(pre  + exception.getClass().getSimpleName() + ": " + exception.getMessage() + post);
    }

    ///
}
