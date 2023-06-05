package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//________________________________________________________________________________________________________________________________________

/**
 * This class holds the actual log printing methods.
 * {@link LogPrinter} has no state.
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

        String prefix = "[ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " ] -------- [ ";
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

        String prefix = "[ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " ] -------- [ ";
        String level = log_level.getValue();

        try {

            file_writer.write(prefix + level + " ] -------- : " + message + "\n");
            file_writer.flush();
        }

        catch(IOException exc) {

            printToConsole("Thread: " + Thread.currentThread().getName() + " Id: " + Thread.currentThread().threadId() + " threw " + exc.getClass().getName() + " message: " + exc.getMessage(), log_level);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
