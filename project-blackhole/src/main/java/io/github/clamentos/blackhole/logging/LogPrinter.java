package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import java.io.BufferedWriter;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//________________________________________________________________________________________________________________________________________

/**
 * Static class that holds the actual log printing methods.
*/
public class LogPrinter {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is partially thread safe.</b></p>
     * <p>(Thread safe on a line-per-line basys. Interleaved lines are possible)</p>
     * Prints the message to the standard out with the given log level.
     * @param message : The message to be printed.
     * @param log_level : The severity of the message.
     * @throws NullPointerException If the {@code log_level} is null.
    */
    public static void printToConsole(String message, LogLevel log_level) {

        String now =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
        String head = "[ " + now + " ]";
        String level = "[ " + log_level.getColor() + log_level.getValue() + "\u001B[0m ]";
        String msg = log_level.getColor() + message + "\u001B[0m";

        System.out.println(level + " - " + head + " - " + msg);
    }

    /**
     * <p><b>This method is partially thread safe.</b></p>
     * <p>(Thread safe on a line-per-line basys. Interleaved lines are possible)</p>
     * Prints the message to the specified file with the given log level.
     * @param message : The message to be printed
     * @param log_level : The severity of the message.
     * @param file_writer : The destination file.
     * @throws NullPointerException If either {@code log_level} or {@code file_writer} are null.
     * @returns The number of bytes written to the file.
    */
    public static long printToFile(String message, LogLevel log_level, BufferedWriter file_writer) {

        String now =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
        String head = "[ " + now + " ]";
        String level = "[ " + log_level.getValue() + " ]";
        String actual = level + " - " + head + " - " + message + "\n";

        try {

            file_writer.write(actual);
            file_writer.flush();
            
            return(actual.length());
        }

        catch(IOException exc) {

            printToConsole("Could not write to log file, IOException: " + exc.getMessage() + ". Writing to console instead", LogLevel.WARNING);
            printToConsole(message, log_level);

            return(0);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
