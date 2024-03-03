package io.github.clamentos.blackhole.framework.implementation.persistence.models;

///
import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entity;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.query.QueryBinder;

///
/**
 * <h3>System Diagnostics</h3>
 * System snapshot entity. Corresponds to the {@code SystemDiagnostics} table.
*/
public final record SystemDiagnostics(

    ///
    /** The instantiation timestamp of {@code this}. */
    long creation_date,

    /** The uptime of the system in milliseconds. */
    long uptime,

    /** The number of virtual threads currently active in the system. */
    int vritual_threads,

    /** The number of OS threads currently active in the system. */
    int carrier_threads,

    /** The current amount of heap used. */
    long memory_used,

    /** The maximum available heap. */
    long memory_free,

    /** The number of cache hits. */
    int cache_hits,

    /** The number of cache misses. */
    int cache_misses,

    /** The number of successfull database queries. */
    int database_queries_ok,

    /** The number of failed database queries. */
    int database_queries_ko,

    /** The number of user sessions created. */
    int sessions_created,

    /** The number of user sessions destroyed. */
    int sessions_destroyed,

    /** The number of users that logged in. */
    int logged_users,

    /** The number of successfull {@code CREATE} requests. */
    int create_requests_ok,

    /** The number of failed {@code CREATE} requests. */
    int create_requests_ko,

    /** The number of successfull {@code READ} requests. */
    int read_requests_ok,

    /** The number of failed {@code READ} requests. */
    int read_requests_ko,

    /** The number of successfull {@code UPDATE} requests. */
    int update_requests_ok,

    /** The number of failed {@code UPDATE} requests. */
    int update_requests_ko,

    /** The number of successfull {@code DELETE} requests. */
    int delete_requests_ok,

    /** The number of failed {@code DELETE} requests. */
    int delete_requests_ko,

    /** The number of failed requests before the request method is known. */
    int unknown_requests_ko,

    /** The number of responses sent successfully. */
    int responses_sent_ok,

    /** The number of responses that the server attempted to sent but failed. */
    int responses_sent_ko,

    /** The number of accepted socket connections. */
    int sockets_accepted,

    /** The number of refused socket connections. */
    int sockets_refused,

    /** The number of closed socket connections. */
    int sockets_closed

    ///
) implements Entity {

    ///
    /**
     * {@inheritDoc}
     * @throws NullPointerException If {@code query_binder} is {@code null}.
    */
    @Override
    public void bindForInsert(QueryBinder query_binder) throws NullPointerException {

        query_binder.bindLong(creation_date);
        query_binder.bindLong(uptime);
        query_binder.bindInt(vritual_threads);
        query_binder.bindInt(carrier_threads);
        query_binder.bindLong(memory_used);
        query_binder.bindLong(memory_free);
        query_binder.bindInt(cache_hits);
        query_binder.bindInt(cache_misses);
        query_binder.bindInt(database_queries_ok);
        query_binder.bindInt(database_queries_ko);
        query_binder.bindInt(sessions_created);
        query_binder.bindInt(sessions_destroyed);
        query_binder.bindInt(logged_users);
        query_binder.bindInt(create_requests_ok);
        query_binder.bindInt(read_requests_ok);
        query_binder.bindInt(update_requests_ok);
        query_binder.bindInt(delete_requests_ok);
        query_binder.bindInt(create_requests_ko);
        query_binder.bindInt(read_requests_ko);
        query_binder.bindInt(update_requests_ko);
        query_binder.bindInt(delete_requests_ko);
        query_binder.bindInt(unknown_requests_ko);
        query_binder.bindInt(responses_sent_ok);
        query_binder.bindInt(responses_sent_ko);
        query_binder.bindInt(sockets_accepted);
        query_binder.bindInt(sockets_refused);
        query_binder.bindInt(sockets_closed);
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws NullPointerException If {@code query_binder} is {@code null}.
    */
    @Override
    public void bindForUpdate(QueryBinder query_binder, long fields) throws NullPointerException {

        if((fields & 0b0000000000000000000000000010) > 0) query_binder.bindLong(uptime);
        if((fields & 0b0000000000000000000000000100) > 0) query_binder.bindInt(vritual_threads);
        if((fields & 0b0000000000000000000000001000) > 0) query_binder.bindInt(carrier_threads);
        if((fields & 0b0000000000000000000000010000) > 0) query_binder.bindLong(memory_used);
        if((fields & 0b0000000000000000000000100000) > 0) query_binder.bindLong(memory_free);
        if((fields & 0b0000000000000000000001000000) > 0) query_binder.bindInt(cache_hits);
        if((fields & 0b0000000000000000000010000000) > 0) query_binder.bindInt(cache_misses);
        if((fields & 0b0000000000000000000100000000) > 0) query_binder.bindInt(database_queries_ok);
        if((fields & 0b0000000000000000001000000000) > 0) query_binder.bindInt(database_queries_ko);
        if((fields & 0b0000000000000000010000000000) > 0) query_binder.bindInt(sessions_created);
        if((fields & 0b0000000000000000100000000000) > 0) query_binder.bindInt(sessions_destroyed);
        if((fields & 0b0000000000000001000000000000) > 0) query_binder.bindInt(logged_users);
        if((fields & 0b0000000000000010000000000000) > 0) query_binder.bindInt(create_requests_ok);
        if((fields & 0b0000000000000100000000000000) > 0) query_binder.bindInt(read_requests_ok);
        if((fields & 0b0000000000001000000000000000) > 0) query_binder.bindInt(update_requests_ok);
        if((fields & 0b0000000000010000000000000000) > 0) query_binder.bindInt(delete_requests_ok);
        if((fields & 0b0000000000100000000000000000) > 0) query_binder.bindInt(create_requests_ko);
        if((fields & 0b0000000001000000000000000000) > 0) query_binder.bindInt(read_requests_ko);
        if((fields & 0b0000000010000000000000000000) > 0) query_binder.bindInt(update_requests_ko);
        if((fields & 0b0000000100000000000000000000) > 0) query_binder.bindInt(delete_requests_ko);
        if((fields & 0b0000001000000000000000000000) > 0) query_binder.bindInt(unknown_requests_ko);
        if((fields & 0b0000010000000000000000000000) > 0) query_binder.bindInt(responses_sent_ok);
        if((fields & 0b0000100000000000000000000000) > 0) query_binder.bindInt(responses_sent_ko);
        if((fields & 0b0001000000000000000000000000) > 0) query_binder.bindInt(sockets_accepted);
        if((fields & 0b0010000000000000000000000000) > 0) query_binder.bindInt(sockets_refused);
        if((fields & 0b0100000000000000000000000000) > 0) query_binder.bindInt(sockets_closed);

        query_binder.bindLong(creation_date);
    }

    ///
}
