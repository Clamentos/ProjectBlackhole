package io.github.clamentos.blackhole.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.configuration.Constants;

public class Logger {
    
    private static final Logger INSTANCE = new Logger();

    private final LogLevel MIN_LOG_LEVEL;

    private List<LinkedBlockingQueue<Log>> queues;
    private List<LogTask> log_tasks;

    private Logger() {

        MIN_LOG_LEVEL = ConfigurationProvider.getInstance().getConstant(Constants.MIN_LOG_LEVEL, LogLevel.class);

        queues = new ArrayList<>();
        log_tasks = new ArrayList<>();

        queues.add(new LinkedBlockingQueue<>());
        queues.add(new LinkedBlockingQueue<>());

        log_tasks.add(new LogTask(queues.get(0)));
        log_tasks.add(new LogTask(queues.get(1)));
    }

    public static Logger getInstance() {

        return(INSTANCE);
    }

    public void log(String message, LogLevel severity) {

        Log log;

        if(MIN_LOG_LEVEL.compareTo(severity) <= 0) {

            log = new Log(message, severity);
            // put in one of the queues
        }
    }
}
