package io.github.clamentos.blackhole.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogPrinter {
    
    public static void printToConsole(String message, LogLevel log_level) {

        String prefix = "[ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " ] -------- [ ";
        String color = log_level.getColor();
        String level = log_level.getValue();

        System.out.println(prefix + color + level + "\u001B[0m" + " ] -------- : " + color + message + "\u001B[0m");
    }

    public static void printToFile(String message, LogLevel log_level, BufferedWriter file_writer) throws IOException {

        String prefix = "[ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " ] -------- [ ";
        String level = log_level.getValue();

        file_writer.write(prefix + level + " ] -------- : " + message + "\n");
        file_writer.flush();
    }
}
