package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import java.io.BufferedWriter;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//________________________________________________________________________________________________________________________________________

/**
 * This class holds the actual (static) log printing methods.
*/
public class LogPrinter {

    //____________________________________________________________________________________________________________________________________

    /**
     * Prints the message to the standard out with the given log level.
     * This method is thread safe on a line-per-line basys.
     * This means that lines cannot be corrupted, but interleaved lines are possible.
     * @param message : The message to be printed.
     * @param log_level : The severity of the message.
     * @throws NullPointerException if the {@code log_level} is null.
    */
    public static void printToConsole(String message, LogLevel log_level) {

        String now =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        String head = "[ " + now + " ]";
        String level = "[ " + log_level.getColor() + log_level.getValue() + "\u001B[0m ]";
        String msg = log_level.getColor() + message + "\u001B[0m";

        System.out.println(head + " --- " + level + " --- " + msg);
    }

    /**
     * Prints the message to the specified file with the given log level.
     * This method is thread safe on a line-per-line basys.
     * This means that lines cannot be corrupted, but interleaved lines are possible.
     * @param message : The message to be printed
     * @param log_level : The severity of the message.
     * @param file_writer : The destination file.
     * @throws NullPointerException if either {@code log_level} or {@code file_writer} are null.
    */
    public static void printToFile(String message, LogLevel log_level, BufferedWriter file_writer) {

        String now =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        String head = "[ " + now + " ]";
        String level = "[ " + log_level.getValue() + " ]";

        try {

            file_writer.write(head + " --- " + level + " --- " + message);
        }

        catch(IOException exc) {

            printToConsole("Could not write to log file, IOException thrown: " + exc.getMessage(), LogLevel.WARNING);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
