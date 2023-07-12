package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import java.io.IOException;
import java.text.SimpleDateFormat;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Static class that holds the actual log printing methods.</p>
 * <p>The logs will have the following format:</p>
 * {@code [ERROR]-[20/10/2023]-[14:10:34.123]-[14:10:36.345]-[1234567890]-[...]}
 * <ol>
 *     <li>{@code [ERROR]}: the log level.</li>
 *     <li>{@code [20/10/2023]}: the log creation date.</li>
 *     <li>{@code [14:10:34.123]}: the log creation time.</li>
 *     <li>{@code [14:10:36.345]}: the log printing time.</li>
 *     <li>{@code [1234567890]}: the unique log id.</li>
 *     <li>{@code [...]}: the actual log message.</li>
 * </ol>
*/
public class LogPrinter {
    
    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is partially thread safe.</b></p>
     * <p>(Thread safe on a line-per-line basys. Interleaved lines are possible).</p>
     * Prints the log to the standard output (console) with formatting.
     * @param log : The {@link Log} to print. If {@code log} is {@code null}, this method
     *              will not do anyting.
    */
    public static void printToConsole(Log log) {

        String level;
        String partial;
        String message;

        if(log == null) {

            return;
        }

        level = "[" + log.log_level().getColor() + log.log_level().getValue() + "\u001B[0m]-";
        message = "[" + log.log_level().getColor() + log.message() + "\u001B[0m]";
        partial = partialString(log);

        System.out.println(level + partial + message);
    }

    /**
     * <p><b>This method is partially thread safe.</b></p>
     * <p>(Thread safe on a line-per-line basys. Interleaved lines are possible).</p>
     * Prints the message to the log file.
     * @param log : The {@link Log} to print. If {@code log} is {@code null}, this method
     *              will not do anyting.
    */
    public static void printToFile(Log log) {

        LogFileManager file = LogFileManager.getInstance();

        String level;
        String partial;
        String message;

        if(log == null) {

            return;
        }

        level = "[" + log.log_level().getValue() + "]-";
        partial = partialString(log);
        message = "[" + log.message() + "]";

        try {

            file.write(level + partial + message);
        }

        catch(IOException exc) {

            printToConsole(new Log(
                
                "LogPrinter.printToFile > Could not write to log file, IOException: " + exc.getMessage() + ". Writing to console instead",
                LogLevel.WARNING
            ));

            printToConsole(log);
        }
    }

    //____________________________________________________________________________________________________________________________________

    // prepare the partial message
    private static String partialString(Log log) {

        SimpleDateFormat date_formatter;
        SimpleDateFormat time_formatter;

        String date;
        String time_start;
        String time_end;
        String id;

        date_formatter = new SimpleDateFormat("dd/MM/yyyy");
        time_formatter = new SimpleDateFormat("HH:mm:ss.SSS");

        date = "[" + date_formatter.format(log.creation_date()) + "]-";
        time_start = "[" + time_formatter.format(log.creation_date()) + "]-";
        time_end = "[" + time_formatter.format(System.currentTimeMillis()) + "]-";
        id = "[" + log.id() + "]-";

        return(date + time_start + time_end + id);
    }

    //____________________________________________________________________________________________________________________________________
}