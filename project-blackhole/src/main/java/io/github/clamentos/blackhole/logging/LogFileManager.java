package io.github.clamentos.blackhole.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.configuration.Constants;

public class LogFileManager {

    private static final LogFileManager INSTANCE = new LogFileManager();
    private final int MAX_LOG_FILE_SIZE = ConfigurationProvider.getInstance().getConstant(Constants.MAX_LOG_FILE_SIZE, Integer.class);

    private BufferedWriter file_writer;
    private long file_size;

    private LogFileManager() {

        findEligible();
    }

    public static LogFileManager getInstance() {

        return(INSTANCE);
    }

    // maybe sync all?
    public void write(String data) throws IOException {

        if(file_size >= MAX_LOG_FILE_SIZE) {

            findEligible();
        }

        file_writer.write(data);
        file_writer.flush();
        file_size += data.length();
    }

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

            if((found > 0) && (files[found - 1].length() < MAX_LOG_FILE_SIZE)) {

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
}
