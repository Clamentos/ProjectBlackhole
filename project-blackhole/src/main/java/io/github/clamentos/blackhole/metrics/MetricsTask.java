package io.github.clamentos.blackhole.metrics;

import io.github.clamentos.blackhole.persistence.pool.ConnectionPool;
import io.github.clamentos.blackhole.persistence.pool.PooledConnection;
import io.github.clamentos.blackhole.scaffolding.tasks.ContinuousTask;
import java.sql.Connection;

public final class MetricsTask extends ContinuousTask {

    private ConnectionPool pool;
    
    public MetricsTask(long id) {

        super(id);
    }
    
    @Override
    public void setup() {

        pool = ConnectionPool.getInstance();
    }

    @Override
    public void work() {

        MetricsSnapshot snapshot;
        PooledConnection connection;

        try {

            Thread.sleep(300_000);
            snapshot = MetricsService.getInstance().sample();
            connection = pool.aquireConnection(0);

            //...
            // sample logs from file
            // insert into system diagnostics
            // get the id ^^^
            // insert into logs
            //...

            pool.releaseConnection(connection, 0);
        }

        catch(Exception exc) {

            // InterruptedException
            // IllegalStateException
            // SQLException
        }
    }

    @Override
    public void terminate() {

        //...
    }
}
