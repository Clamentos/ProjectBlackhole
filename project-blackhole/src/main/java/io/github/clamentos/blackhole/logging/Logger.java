package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.configuration.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

public class Logger {
    
    private static final Logger INSTANCE = new Logger();

    private final LogLevel MIN_LOG_LEVEL;

    private List<LinkedBlockingQueue<Log>> queues;
    private List<LogTask> log_tasks;

    //____________________________________________________________________________________________________________________________________

    private Logger() {

        MIN_LOG_LEVEL = ConfigurationProvider.getInstance().getConstant(Constants.MIN_LOG_LEVEL, LogLevel.class);

        queues = new ArrayList<>();
        log_tasks = new ArrayList<>();

        queues.add(new LinkedBlockingQueue<>());
        queues.add(new LinkedBlockingQueue<>());

        log_tasks.add(new LogTask(queues.get(0)));
        log_tasks.add(new LogTask(queues.get(1)));
    }

    //____________________________________________________________________________________________________________________________________

    public static Logger getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    public void log(String message, LogLevel severity) {

        Log log;
        int i;

        if(MIN_LOG_LEVEL.compareTo(severity) <= 0) {

            log = new Log(message, severity);
            i = 0;
            
            while(i < 10) {

                try {

                    if(queues.get((int)(log.id() % queues.size())).offer(log, 10_000, TimeUnit.MILLISECONDS) == true) {

                        return;
                    }

                    else {

                        LogPrinter.printToConsole(new Log(

                            "Logger.log > Could not insert into the log queue, timed out.",
                            LogLevel.ERROR
                        ));
                    }
                }

                catch(InterruptedException exc) {

                    LogPrinter.printToConsole(new Log(

                        "Logger.log > Could not insert into the log queue, InterruptedException: " +
                        exc.getMessage() + " Retrying...",
                        LogLevel.WARNING
                    ));
                }
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
