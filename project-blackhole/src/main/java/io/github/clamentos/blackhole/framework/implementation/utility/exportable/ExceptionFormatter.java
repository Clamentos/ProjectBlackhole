package io.github.clamentos.blackhole.framework.implementation.utility.exportable;

///
/**
 * <h3>Exception Formatter</h3>
 * Provides output formatting methods for logging and printing exceptions.
*/
public final class ExceptionFormatter {

    ///
    /**
     * <p>Formats the given parameters into an output message.</p>
     * Example output: {@code (SomeException: some message) caused by: (OtherException: other message)}
     * @param pre : The message header.
     * @param exception : The target exception.
     * @param post : The message footer.
     * @return The formatted exception message.
     * @apiNote This method recursively expands the cause chain of {@code exception}.
    */
    public static String format(String pre, Throwable exception, String post) {

        StringBuilder builder = new StringBuilder();
        formatInner(exception, builder, false);

        return(pre  + " >> " + builder.toString() + " >> " + post);
    }

    ///.
    /**
     * Builds the message string by recursivery walking the exception cause chain.
     * @param exception : The starting exception.
     * @param builder : The string builder to form the message.
     * @param flag : Reserved for internal use. Should always be called with {@code false}.
    */
    private static void formatInner(Throwable exception, StringBuilder builder, boolean flag) {

        if(exception != null) {

            builder.append(flag ? " caused by: (" : "(");
            builder.append(exception.getClass().getSimpleName());

            if(exception.getMessage() != null) {

                builder.append(": ");
                builder.append(exception.getMessage());
            }

            builder.append(")");
            formatInner(exception.getCause(), builder, true);
        }
    }

    ///
}
