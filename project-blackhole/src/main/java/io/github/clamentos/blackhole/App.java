// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.Repository;
import io.github.clamentos.blackhole.web.server.Server;

//________________________________________________________________________________________________________________________________________

/**
 * Main App class, just some random stuff for now...
 */
public class App {

    //____________________________________________________________________________________________________________________________________

    public static void main(String[] args) {

        Logger logger = Logger.getInstance();

        try {
            
            Repository repo = Repository.getInstance();
            Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
            ConfigurationProvider.initServlets(repo);
            Server web_server = Server.getInstance();
            web_server.start();
        }

        catch(InstantiationException exc) {

            logger.log("Could not fully start the app, InstantiationException: " + exc.getMessage(), LogLevel.ERROR);
            System.exit(1);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
