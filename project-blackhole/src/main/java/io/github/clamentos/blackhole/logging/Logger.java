package io.github.clamentos.blackhole.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Logger {
    
    private static final Logger INSTANCE = new Logger();

    private List<LinkedBlockingQueue<Log>> queues;
    private List<LogTask> log_tasks;

    private Logger() {

        //...

        queues = new ArrayList<>();
        log_tasks = new ArrayList<>();

        queues.add(new LinkedBlockingQueue<>());
        queues.add(new LinkedBlockingQueue<>());

        log_tasks.add(new LogTask(queues.get(0), null)); // writer
        log_tasks.add(new LogTask(queues.get(1), null));
    }
}
