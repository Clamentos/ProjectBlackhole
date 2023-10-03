package io.github.clamentos.blackhole.metrics;

public record MetricsSnapshot(

    int vritual_threads,
    int carrier_threads,
    long memory_used,
    long memory_free,
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
