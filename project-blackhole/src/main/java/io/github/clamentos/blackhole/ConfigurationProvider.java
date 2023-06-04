package io.github.clamentos.blackhole;

import io.github.clamentos.blackhole.logging.LogLevel;

public class ConfigurationProvider {
    
    static final LogLevel DEFAULT_MINIMUM_LOG_LEVEL = LogLevel.INFO;
    static final int DEFAULT_OFFER_TIMEOUT = 1_000;
    static final int DEFAULT_MAX_QUEUE_SIZE = 1_000_000;

    static final int DEFAULT_SERVER_PORT = 8080;
    static final int DEFAULT_REQUEST_WORKERS = 4;
}
