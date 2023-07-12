package io.github.clamentos.blackhole.persistence;

import java.sql.Connection;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {
    
    private static final ConnectionPool INSTANCE = new ConnectionPool();
    private LinkedBlockingQueue<Connection> pool;

    private ConnectionPool() {

        pool = new LinkedBlockingQueue<>();

        for(int i = 0; i < 10; i++) {

            pool.add(/* connection */);
        }
    }

    public static ConnectionPool getInstance() {

        return(INSTANCE);
    }

    public Connection aquireConnection() {

        // check if alive...

        while(true) {

            try {

                return(pool.take());
            }

            catch(InterruptedException exc) {

                //...
            }
        }
    }

    public void releaseConnection(Connection connection) throws IllegalStateException {

        pool.add(connection);
    }
}
