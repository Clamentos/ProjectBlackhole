package io.github.clamentos.blackhole.metrics;

import java.util.concurrent.atomic.AtomicInteger;

public class MetricsService {

    private static final MetricsService INSTANCE = new MetricsService();
    
    private AtomicInteger cache_misses;
    private AtomicInteger database_queries;
    private AtomicInteger sessions_created;
    private AtomicInteger sessions_destroyed;
    private AtomicInteger logged_users;
    private AtomicInteger create_requests;
    private AtomicInteger read_requests;
    private AtomicInteger update_requests;
    private AtomicInteger delete_requests;
    private AtomicInteger responses_sent;
    private AtomicInteger sockets_accepted;
    private AtomicInteger sockets_closed;

    public MetricsService() {

        cache_misses = new AtomicInteger();
        database_queries = new AtomicInteger();
        sessions_created = new AtomicInteger();
        sessions_destroyed = new AtomicInteger();
        logged_users = new AtomicInteger();
        create_requests = new AtomicInteger();
        read_requests = new AtomicInteger();
        update_requests = new AtomicInteger();
        delete_requests = new AtomicInteger();
        responses_sent = new AtomicInteger();
        sockets_accepted = new AtomicInteger();
        sockets_closed = new AtomicInteger();
    }

    protected MetricsSnapshot sample() {

        MetricsSnapshot snapshot = new MetricsSnapshot(

            0,
            0,
            Runtime.getRuntime().totalMemory(),
            Runtime.getRuntime().freeMemory(),
            cache_misses.get(),
            database_queries.get(),
            sessions_created.get(),
            sessions_destroyed.get(),
            logged_users.get(),
            create_requests.get(),
            read_requests.get(),
            update_requests.get(),
            delete_requests.get(),
            responses_sent.get(),
            sockets_accepted.get(),
            sockets_closed.get()
        );

        cache_misses.set(0);
        database_queries.set(0);
        sessions_created.set(0);
        sessions_destroyed.set(0);
        logged_users.set(0);
        create_requests.set(0);
        read_requests.set(0);
        update_requests.set(0);
        delete_requests.set(0);
        responses_sent.set(0);
        sockets_accepted.set(0);
        sockets_closed.set(0);

        return(snapshot);
    }

    public static MetricsService getInstance() {

        return(INSTANCE);
    }

    public void incrementCacheMisses(int amount) {

        cache_misses.addAndGet(amount);
    }

    //...
}
