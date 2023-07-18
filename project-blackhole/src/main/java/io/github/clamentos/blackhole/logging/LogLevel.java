// OK
package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Enumeration of all the possible log levels.</p>
 * Each entry is composed of the log level name + color.
 * <p>The log levels in increasing relevance are:</p>
 * <ol>
 *     <li>{@code DEBUG}: used for debugging.</li>
 *     <li>{@code INFO}: used to give harmless but useful information.</li>
 *     <li>{@code SUCCESS}: used to indicate the positive outcome of a critical process.</li>
 *     <li>{@code NOTE}: used to indicate failures on non critical processes.</li>
 *     <li>{@code WARNING}: used to indicate that a critical process encountered an unusual
 *         situation but was able to recover without service interruptions.</li>
 *     <li>{@code ERROR}: used to indicate that a critical process encountered an unusual or unexpected
 *         situation that was not able to recover without service interruptions.</li>
 * </ol>
*/
public enum LogLevel {
    
    DEBUG("DEBUG  ", "\u001B[30m"),
    INFO("INFO   ", "\u001B[34m"),
    SUCCESS("SUCCESS", "\u001B[32m"),
    NOTE("NOTE   ", "\u001B[35m"),
    WARNING("WARNING", "\u001B[33m"),
    ERROR("ERROR  ", "\u001B[31m");

    //____________________________________________________________________________________________________________________________________

    private String value;
    private String color;

    //____________________________________________________________________________________________________________________________________

    private LogLevel(String value, String color) {

        this.value = value;
        this.color = color;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the associated log level string.
     * @return the log level string (always well-defined).
    */
    public String getValue() {

        return(value);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the associated log level color.
     * @return the log level color (always well-defined).
    */
    public String getColor() {

        return(color);
    }

    //____________________________________________________________________________________________________________________________________
}
