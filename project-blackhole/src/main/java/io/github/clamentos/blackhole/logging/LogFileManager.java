// OK
package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Eager-loaded singleton.</b></p>
 * <p>Log file manager.</p>
 * This class manages the log files in {@code /logs} when writing,
 * including finding the latest eligible log file to write and
 * creating a new one when the size limit is reached.
*/
public class LogFileManager {

    private static final LogFileManager INSTANCE = new LogFileManager();

    private ConfigurationProvider configuration_provider;

    private BufferedWriter file_writer;
    private long file_size;

    //____________________________________________________________________________________________________________________________________

    private LogFileManager() {

        configuration_provider = ConfigurationProvider.getInstance();
        findEligible();
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the {@link LogFileManager} instance created during class loading.
     * @return The {@link ConfigurationProvider} instance.
    */
    public static LogFileManager getInstance() {

        return(INSTANCE);
    }

    /**
     * <p><b>This method is partially thread safe.</b></p>
     * <p>(Thread safe on a line-per-line basys. Interleaved lines are possible).</p>
     * Write to the currently active log file.
     * @param data : The actual data.
     * @throws IOException If the method cannot write the file.
     * @throws NullPointerException If {@code data} is {@code null}.
    */
    // maybe sync all?
    public void write(String data) throws IOException, NullPointerException {

        if(file_size >= configuration_provider.MAX_LOG_FILE_SIZE) {

            findEligible();
        }

        file_writer.write(data);
        file_writer.flush();
        file_size += data.length();
    }

    //____________________________________________________________________________________________________________________________________

    // Finds the most "recent" log file. If there are none, create one.
    private synchronized void findEligible() {

        File[] files;
        long last_modified = 0;
        int found = 0;

        try {

            files = new File("logs/").listFiles();

            for(int i = 0; i < files.length; i++) {

                if(files[i].lastModified() > last_modified) {

                    last_modified = files[i].lastModified();
                    found = i + 1;
                }
            }

            if((found > 0) && (files[found - 1].length() < configuration_provider.MAX_LOG_FILE_SIZE)) {

                file_size = files[found - 1].length();
                file_writer = new BufferedWriter(new FileWriter(files[found - 1], true));

                return;
            }

            createNewLogFile();
        }

        catch(IOException exc) {

            LogPrinter.printToConsole(new Log(

                "LogFile.findEligible > Could not access file, IOException: " + exc.getMessage(),
                LogLevel.ERROR
            ));
        }
    }

    private void createNewLogFile() {

        String name;

        try {

            name = "logs/log" + System.currentTimeMillis() + ".log";

            if(file_writer != null) {

                file_writer.close();
            }

            file_writer = new BufferedWriter(new FileWriter(name));
            file_size = 0;
        }

        catch(IOException exc) {

            LogPrinter.printToConsole(new Log(

                "LogFile.createNewLogFile > Could not access file, IOException: " + exc.getMessage(),
                LogLevel.ERROR
            ));
        }
    }

    //____________________________________________________________________________________________________________________________________
}
