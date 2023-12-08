package io.github.clamentos.blackhole.framework.implementation.logging;

///
import io.github.clamentos.blackhole.framework.implementation.persistence.models.SystemDiagnostics;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.TaskManager;

///.
import java.lang.management.ManagementFactory;

///..
import java.util.concurrent.atomic.AtomicInteger;

///
/**
 * <h3>Metrics tracker</h3>
 * <p>Exposes various metrics that other classes can update as they run.</p>
 * At any point in time a snapshot of the system can be taken via this class.
 * @see SystemDiagnostics
*/
public final class MetricsTracker {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final MetricsTracker INSTANCE = new MetricsTracker();

    ///.
    /** The service used to log notable events. */
    private final Logger logger;

    ///..
    /** The timestamp of instantiation of {@code this}. Used to keep track of the system uptime. */
    private final long timestamp_of_instantiation;

    /** The atomic counter that tracks the number of cache hits. */
    private final AtomicInteger cache_hits;

    /** The atomic counter that tracks the number of cache misses. */
    private final AtomicInteger cache_misses;

    /** The atomic counter that tracks the number of successfull database queries. */
    private final AtomicInteger database_queries_ok;

    /** The atomic counter that tracks the number of failed database queries. */
    private final AtomicInteger database_queries_ko;

    /** The atomic counter that tracks the number of user sessions created. */
    private final AtomicInteger sessions_created;

    /** The atomic counter that tracks the number of user sessions destroyed. */
    private final AtomicInteger sessions_destroyed;

    /** The atomic counter that tracks the number of users that logged in. */
    private final AtomicInteger logged_users;

    /** The atomic counter that tracks the number of successfull {@code CREATE} requests. */
    private final AtomicInteger create_requests_ok;

    /** The atomic counter that tracks the number of failed {@code CREATE} requests. */
    private final AtomicInteger create_requests_ko;

    /** The atomic counter that tracks the number of successfull {@code READ} requests. */
    private final AtomicInteger read_requests_ok;

    /** The atomic counter that tracks the number of failed {@code READ} requests. */
    private final AtomicInteger read_requests_ko;

    /** The atomic counter that tracks the number of successfull {@code UPDATE} requests. */
    private final AtomicInteger update_requests_ok;

    /** The atomic counter that tracks the number of failed {@code UPDATE} requests. */
    private final AtomicInteger update_requests_ko;

    /** The atomic counter that tracks the number of successfull {@code DELETE} requests. */
    private final AtomicInteger delete_requests_ok;

    /** The atomic counter that tracks the number of failed {@code DELETE} requests. */
    private final AtomicInteger delete_requests_ko;

    /** The atomic counter that tracks the number of responses sent. */
    private final AtomicInteger responses_sent;

    /** The atomic counter that tracks the number of accepted socket connections. */
    private final AtomicInteger sockets_accepted;

    /** The atomic counter that tracks the number of closed socket connections. */
    private final AtomicInteger sockets_closed;

    ///
    /** Instantiates a new {@code MetricsTracker} object. */
    private MetricsTracker() {

        timestamp_of_instantiation = System.currentTimeMillis();
        logger = Logger.getInstance();

        cache_hits = new AtomicInteger();
        cache_misses = new AtomicInteger();
        database_queries_ok = new AtomicInteger();
        database_queries_ko = new AtomicInteger();
        sessions_created = new AtomicInteger();
        sessions_destroyed = new AtomicInteger();
        logged_users = new AtomicInteger();
        create_requests_ok = new AtomicInteger();
        create_requests_ko = new AtomicInteger();
        read_requests_ok = new AtomicInteger();
        read_requests_ko = new AtomicInteger();
        update_requests_ok = new AtomicInteger();
        update_requests_ko = new AtomicInteger();
        delete_requests_ok = new AtomicInteger();
        delete_requests_ko = new AtomicInteger();
        responses_sent = new AtomicInteger();
        sockets_accepted = new AtomicInteger();
        sockets_closed = new AtomicInteger();

        TaskManager.getInstance().launchThread(new MetricsTask(), "MetricsTask");
        logger.log("MetricsTracker.new >> Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** @return The {@link MetricsTracker} instance created during class loading. */
    public static MetricsTracker getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Increments the {@code cache_hits} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementCacheHits(int amount) {

        cache_hits.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code cache_misses} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementCacheMisses(int amount) {

        cache_misses.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code database_queries_ok} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementDatabaseQueriesOk(int amount) {

        database_queries_ok.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code database_queries_ko} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementDatabaseQueriesKo(int amount) {

        database_queries_ko.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code sessions_created} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementSessionsCreated(int amount) {

        sessions_created.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code sessions_destroyed} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementSessionsDestroyed(int amount) {

        sessions_destroyed.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code logged_users} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementLoggedUsers(int amount) {

        logged_users.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code create_requests_ok} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementCreateRequestsOk(int amount) {

        create_requests_ok.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code create_requests_ko} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementCreateRequestsKo(int amount) {

        create_requests_ko.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code read_requests_ok} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementReadRequestsOk(int amount) {

        read_requests_ok.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code read_requests_ko} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementReadRequestsKo(int amount) {

        read_requests_ko.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code update_requests_ok} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementUpdateRequestsOk(int amount) {

        update_requests_ok.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code update_requests_ko} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementUpdateRequestsKo(int amount) {

        update_requests_ko.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code delete_requests_ok} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementDeleteRequestsOk(int amount) {

        delete_requests_ok.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code delete_requests_ko} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementDeleteRequestsKo(int amount) {

        delete_requests_ko.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code responses_sent} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementResponsesSent(int amount) {

        responses_sent.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code sockets_accepted} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementSocketsAccepted(int amount) {

        sockets_accepted.addAndGet(amount);
    }

    ///..
    /**
     * Increments the {@code sockets_closed} metric by {@code amount}.
     * @param amount : The amount.
    */
    public void incrementSocketsClosed(int amount) {

        sockets_closed.addAndGet(amount);
    }

    ///.
    /** @return A {@link SystemDiagnostics} object representing the current status of the system. */
    protected SystemDiagnostics sample() {

        int cache_hits = this.cache_hits.get();
        this.cache_hits.set(0);
        int cache_misses = this.cache_misses.get();
        this.cache_misses.set(0);
        int database_queries_ok = this.database_queries_ok.get();
        this.database_queries_ok.set(0);
        int database_queries_ko = this.database_queries_ko.get();
        this.database_queries_ko.set(0);
        int sessions_created = this.sessions_created.get();
        this.sessions_created.set(0);
        int sessions_destroyed = this.sessions_destroyed.get();
        this.sessions_destroyed.set(0);
        int logged_users = this.logged_users.get();
        this.logged_users.set(0);
        int create_requests_ok = this.create_requests_ok.get();
        this.create_requests_ok.set(0);
        int create_requests_ko = this.create_requests_ko.get();
        this.create_requests_ko.set(0);
        int read_requests_ok = this.read_requests_ok.get();
        this.read_requests_ok.set(0);
        int read_requests_ko = this.read_requests_ko.get();
        this.read_requests_ko.set(0);
        int update_requests_ok = this.update_requests_ok.get();
        this.update_requests_ok.set(0);
        int update_requests_ko = this.update_requests_ko.get();
        this.update_requests_ko.set(0);
        int delete_requests_ok = this.delete_requests_ok.get();
        this.delete_requests_ok.set(0);
        int delete_requests_ko = this.delete_requests_ko.get();
        this.delete_requests_ko.set(0);
        int responses_sent = this.responses_sent.get();
        this.responses_sent.set(0);
        int sockets_accepted = this.sockets_accepted.get();
        this.sockets_accepted.set(0);
        int sockets_closed = this.sockets_closed.get();
        this.sockets_closed.set(0);

        long now = System.currentTimeMillis();

        SystemDiagnostics snapshot = new SystemDiagnostics(

            now, now - timestamp_of_instantiation,
            TaskManager.getInstance().getVirtualThreadCount(),
            ManagementFactory.getThreadMXBean().getThreadCount(),
            ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed(),
            ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax(),
            cache_hits, cache_misses, database_queries_ok, database_queries_ko,
            sessions_created, sessions_destroyed, logged_users, create_requests_ok,
            create_requests_ko, read_requests_ok, read_requests_ko, update_requests_ok,
            update_requests_ko, delete_requests_ok, delete_requests_ko, responses_sent,
            sockets_accepted, sockets_closed
        );

        return(snapshot);
    }

    ///
}
