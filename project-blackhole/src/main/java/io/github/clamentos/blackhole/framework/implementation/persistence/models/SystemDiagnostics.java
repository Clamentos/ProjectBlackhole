package io.github.clamentos.blackhole.framework.implementation.persistence.models;

///
import io.github.clamentos.blackhole.framework.implementation.logging.MetricsTask;
import io.github.clamentos.blackhole.framework.implementation.logging.MetricsTracker;

///..
import io.github.clamentos.blackhole.framework.scaffolding.cache.Cacheability;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entity;

///.
import java.sql.PreparedStatement;
import java.sql.SQLException;

///..
import java.util.List;

///
/**
 * <h3>System diagnostics</h3>
 * System snapshot entity. Corresponds to the {@code SystemDiagnostics} table.
 * @see MetricsTask
 * @see MetricsTracker
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

    /** The number of responses sent. */
    int responses_sent,

    /** The number of accepted socket connections. */
    int sockets_accepted,

    /** The number of closed socket connections. */
    int sockets_closed

    ///
) implements Entity {

    ///
    /** {@inheritDoc} */
    @Override
    public String getTableName() {

        return("\"SystemDiagnostics\"");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public List<String> getColumnNames() {

        return(List.of(

            "\"creation_date\"",
            "\"uptime\"",
            "\"vritual_threads\"",
            "\"carrier_threads\"",
            "\"memory_used\"",
            "\"memory_free\"",
            "\"cache_hits\"",
            "\"cache_misses\"",
            "\"database_queries_ok\"",
            "\"database_queries_ko\"",
            "\"sessions_created\"",
            "\"sessions_destroyed\"",
            "\"logged_users\"",
            "\"create_requests_ok\"",
            "\"create_requests_ko\"",
            "\"read_requests_ok\"",
            "\"read_requests_ko\"",
            "\"update_requests_ok\"",
            "\"update_requests_ko\"",
            "\"delete_requests_ok\"",
            "\"delete_requests_ko\"",
            "\"responses_sent\"",
            "\"sockets_accepted\"",
            "\"sockets_closed\""
        ));
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public boolean usesAutoKey() {

        return(false);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindForInsert(PreparedStatement statement) throws SQLException {

        statement.setLong(1, creation_date);
        statement.setLong(2, uptime);
        statement.setInt(3, vritual_threads);
        statement.setInt(4, carrier_threads);
        statement.setLong(5, memory_used);
        statement.setLong(6, memory_free);
        statement.setInt(7, cache_hits);
        statement.setInt(8, cache_misses);
        statement.setInt(9, database_queries_ok);
        statement.setInt(10, database_queries_ko);
        statement.setInt(11, sessions_created);
        statement.setInt(12, sessions_destroyed);
        statement.setInt(13, logged_users);
        statement.setInt(14, create_requests_ok);
        statement.setInt(15, read_requests_ok);
        statement.setInt(16, update_requests_ok);
        statement.setInt(17, delete_requests_ok);
        statement.setInt(18, create_requests_ko);
        statement.setInt(19, read_requests_ko);
        statement.setInt(20, update_requests_ko);
        statement.setInt(21, delete_requests_ko);
        statement.setInt(22, responses_sent);
        statement.setInt(23, sockets_accepted);
        statement.setInt(24, sockets_closed);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindForUpdate(PreparedStatement statement, long fields) throws SQLException {

        int idx = 1;

        if((fields & 0b0000000000000000000000010) > 0) statement.setLong(idx++, uptime);
        if((fields & 0b0000000000000000000000100) > 0) statement.setInt(idx++, vritual_threads);
        if((fields & 0b0000000000000000000001000) > 0) statement.setInt(idx++, carrier_threads);
        if((fields & 0b0000000000000000000010000) > 0) statement.setLong(idx++, memory_used);
        if((fields & 0b0000000000000000000100000) > 0) statement.setLong(idx++, memory_free);
        if((fields & 0b0000000000000000001000000) > 0) statement.setInt(idx++, cache_hits);
        if((fields & 0b0000000000000000010000000) > 0) statement.setInt(idx++, cache_misses);
        if((fields & 0b0000000000000000100000000) > 0) statement.setInt(idx++, database_queries_ok);
        if((fields & 0b0000000000000001000000000) > 0) statement.setInt(idx++, database_queries_ko);
        if((fields & 0b0000000000000010000000000) > 0) statement.setInt(idx++, sessions_created);
        if((fields & 0b0000000000000100000000000) > 0) statement.setInt(idx++, sessions_destroyed);
        if((fields & 0b0000000000001000000000000) > 0) statement.setInt(idx++, logged_users);
        if((fields & 0b0000000000010000000000000) > 0) statement.setInt(idx++, create_requests_ok);
        if((fields & 0b0000000000100000000000000) > 0) statement.setInt(idx++, read_requests_ok);
        if((fields & 0b0000000001000000000000000) > 0) statement.setInt(idx++, update_requests_ok);
        if((fields & 0b0000000010000000000000000) > 0) statement.setInt(idx++, delete_requests_ok);
        if((fields & 0b0000000100000000000000000) > 0) statement.setInt(idx++, create_requests_ko);
        if((fields & 0b0000001000000000000000000) > 0) statement.setInt(idx++, read_requests_ko);
        if((fields & 0b0000010000000000000000000) > 0) statement.setInt(idx++, update_requests_ko);
        if((fields & 0b0000100000000000000000000) > 0) statement.setInt(idx++, delete_requests_ko);
        if((fields & 0b0001000000000000000000000) > 0) statement.setInt(idx++, responses_sent);
        if((fields & 0b0010000000000000000000000) > 0) statement.setInt(idx++, sockets_accepted);
        if((fields & 0b0100000000000000000000000) > 0) statement.setInt(idx++, sockets_closed);

        statement.setLong(idx, creation_date);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public Cacheability cacheable() {

        return(Cacheability.NEVER);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public int getCacheabilitySizeLimit() {

        return(-1);
    }

    ///
}
