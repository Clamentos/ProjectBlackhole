package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import java.sql.Connection;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

// TODO: finish
public class ConnectionPool {
    
    private static final ConnectionPool INSTANCE = new ConnectionPool();
    private LinkedBlockingQueue<Connection> pool;

    //____________________________________________________________________________________________________________________________________

    private ConnectionPool() {

        pool = new LinkedBlockingQueue<>();

        /*for(int i = 0; i < 10; i++) {

            pool.add();
        }*/
    }

    //____________________________________________________________________________________________________________________________________

    public static ConnectionPool getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

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

    //____________________________________________________________________________________________________________________________________

    public void releaseConnection(Connection connection) throws IllegalStateException {

        pool.add(connection);
    }

    //____________________________________________________________________________________________________________________________________
}
