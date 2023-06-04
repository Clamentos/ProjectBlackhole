// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.RequestPool;
import io.github.clamentos.blackhole.web.Server;

import java.io.IOException;

/**
 * Main App class, just some random stuff for now...
 */
public class App {

    public static void main(String[] args) throws IOException, InterruptedException {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        Logger logger = new Logger(
            
            ConfigurationProvider.DEFAULT_MINIMUM_LOG_LEVEL,
            ConfigurationProvider.DEFAULT_OFFER_TIMEOUT,
            ConfigurationProvider.DEFAULT_MAX_QUEUE_SIZE
        );

        RequestPool request_pool = new RequestPool(ConfigurationProvider.DEFAULT_REQUEST_WORKERS);
        Server server = new Server(ConfigurationProvider.DEFAULT_SERVER_PORT, request_pool);

        //...
    }
}
