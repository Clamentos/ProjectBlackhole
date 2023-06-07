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

        String prefix = "[ " + Thread.currentThread().getName() + ": " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " ] -------- [ ";
        String color = log_level.getColor();
        String level = log_level.getValue();

        System.out.println(prefix + color + level + "\u001B[0m" + " ] -------- : " + color + message + "\u001B[0m");
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

        String prefix = "[ " + Thread.currentThread().getName() + ": " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " ] -------- [ ";
        String level = log_level.getValue();

        try {

            file_writer.write(prefix + level + " ] -------- : " + message + "\n");
        }

        catch(IOException exc) {

            printToConsole("Could not write to log file, IOException thrown: " + exc.getMessage(), LogLevel.WARNING);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
