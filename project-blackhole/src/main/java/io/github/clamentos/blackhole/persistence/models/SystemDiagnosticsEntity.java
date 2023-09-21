package io.github.clamentos.blackhole.persistence.models;

public record SystemDiagnosticsEntity(

    long id,
    int creation_date,
    String logs,
    int threads,
    int memory_used,
    int memory_free,
    int cache_misses,
    int database_queries,
    int sessions_created,
    int sessions_destroyed,
    int logged_users,
    int create_requests,
    int read_requests,
    int update_requests,
    int delete_requests,
    int responses_sent,
    int sockets_accepted,
    int sockets_closed

) {}
