package io.github.clamentos.blackhole.framework.implementation.persistence.models;

///
import io.github.clamentos.blackhole.framework.scaffolding.persistence.cache.Cacheability;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entities;

///
/**
 * <h3>System Entities</h3>
 * Enumeration that describes some internal database system entities.
*/
public enum SystemEntities implements Entities<SystemEntities> {

    ///
    LOG_ENTITY(

        "\"Logs\"",

        new String[]{

            "\"id\"",
            "\"log_id\"",
            "\"creation_date\"",
            "\"log_level\"",
            "\"message\""
        },

        true,
        Cacheability.NEVER,
        -1
    ),

    ///..
    SYSTEM_DIAGNOSTICS(

        "\"SystemDiagnostics\"",

        new String[]{

            "\"creation_date\"",
            "\"uptime\"",
            "\"virtual_threads\"",
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
            "\"unknown_requests_ko\"",
            "\"responses_sent_ok\"",
            "\"responses_sent_ko\"",
            "\"sockets_accepted\"",
            "\"sockets_refused\"",
            "\"sockets_closed\""
        },

        false,
        Cacheability.NEVER,
        -1
    );

    ///
    /** The associated database table name. */
    private final String table_name;

    /** The associated database column names. */
    private final String[] column_names;

    /** The primary key generation strategy. */
    private final boolean auto_key;

    /** The cacheability level. */
    private final Cacheability cacheability;

    /** The cacheability size limit. */
    private final int size_limit;

    ///
    /**
     * Instantiates a new {@code SystemEntities} object.
     * @param table_name : The associated database table name.
     * @param column_names : The associated database column names.
     * @param auto_key : The primary key generation strategy.
     * @param cacheability : The cacheability level.
     * @param size_limit : The cacheability size limit.
    */
    private SystemEntities(String table_name, String[] column_names, boolean auto_key, Cacheability cacheability, int size_limit) {

        this.table_name = table_name;
        this.column_names = column_names;
        this.auto_key = auto_key;
        this.cacheability = cacheability;
        this.size_limit = size_limit;
    }

    ///
    /** {@inheritDoc} */
    @Override
    public String getTableName() {

        return(table_name);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public String[] getColumnNames() {

        return(column_names);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public boolean usesAutoKey() {

        return(auto_key);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public Cacheability getCacheability() {

        return(cacheability);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public int getCacheabilitySizeLimit() {

        return(size_limit);
    }

    ///
}
