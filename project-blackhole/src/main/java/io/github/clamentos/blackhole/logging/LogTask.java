package io.github.clamentos.blackhole.logging;

import java.io.BufferedWriter;
import java.util.concurrent.LinkedBlockingQueue;

public class LogTask implements Runnable {

    private LinkedBlockingQueue<Log> queue;
    private BufferedWriter file_writer;

    public LogTask(LinkedBlockingQueue<Log> queue, BufferedWriter file_writer) {

        this.queue = queue;
        this.file_writer = file_writer;
    }
    
    @Override
    public void run() {

        //...
    }
}
