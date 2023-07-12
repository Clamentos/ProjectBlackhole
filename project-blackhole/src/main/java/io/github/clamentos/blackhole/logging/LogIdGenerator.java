package io.github.clamentos.blackhole.logging;

import java.util.concurrent.atomic.AtomicLong;

public class LogIdGenerator {
    
    private static final LogIdGenerator INSTANCE = new LogIdGenerator();
    private AtomicLong id;

    private LogIdGenerator() {

        id.set(0);
    }

    public static LogIdGenerator getInstance() {

        return(INSTANCE);
    }

    public long getNext() {

        return(id.getAndIncrement());
    }
}
