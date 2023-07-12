package io.github.clamentos.blackhole.logging;

import java.util.Date;

public record Log(
    
    String message,
    LogLevel log_level,
    Date creation_date,
    long id
) {

    public Log(String message, LogLevel log_level) {

        this(
            
            message,
            log_level,
            new Date(System.currentTimeMillis()),
            LogIdGenerator.getInstance().getNext()
        );
    }
}