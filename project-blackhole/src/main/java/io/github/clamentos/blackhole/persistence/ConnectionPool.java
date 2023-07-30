package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

// TODO: finish
public class ConnectionPool {
    
    private static final ConnectionPool INSTANCE = new ConnectionPool();
    
    private Logger logger;
    private ConfigurationProvider configuration_provider;

    private LinkedBlockingQueue<Connection> pool;

    //____________________________________________________________________________________________________________________________________

    private ConnectionPool() {

        configuration_provider = ConfigurationProvider.getInstance();
        logger = Logger.getInstance();

        pool = new LinkedBlockingQueue<>(configuration_provider.NUM_DB_CONNECTIONS);

        try {

            Connection db_connection = createFresh();

            // Set timeout and stuff...

            for(int i = 0; i < configuration_provider.NUM_DB_CONNECTIONS; i++) {

                pool.add(db_connection);
            }
        }

        catch(SQLException exc) {

            logger.log("...", LogLevel.ERROR);
            System.exit(1);
        }
    }

    //____________________________________________________________________________________________________________________________________

    protected static ConnectionPool getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    protected Connection aquireConnection() {

        Connection db_connection;

        while(true) {

            try {

                db_connection = pool.take();

                if(db_connection.isValid(configuration_provider.DB_CONNECTION_TIMEOUT) == false) {

                    db_connection = createFresh();
                }

                return(db_connection);
            }

            catch(InterruptedException | SQLException exc) {

                //...
            }
        }
    }

    protected void releaseConnection(Connection connection) throws IllegalStateException {

        pool.add(connection);
    }

    //____________________________________________________________________________________________________________________________________

    private Connection createFresh() throws SQLException {

        return(DriverManager.getConnection(

            configuration_provider.DB_ADDRESS,
            configuration_provider.DB_USERNAME,
            configuration_provider.DB_PASSWORD
        ));
    }

    //____________________________________________________________________________________________________________________________________
}
