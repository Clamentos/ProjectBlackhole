// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.web.server.Server;

//________________________________________________________________________________________________________________________________________

/**
 * Main App class, just some random stuff for now...
 */
public class App {

    //____________________________________________________________________________________________________________________________________

    public static void main(String[] args) {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        ConfigurationProvider.init();

        Server web_server = Server.getInstance();
        web_server.start();
    }

    //____________________________________________________________________________________________________________________________________
}
