package io.github.clamentos.blackhole.persistence.entities;

import java.sql.Blob;

/**
 * <p><b>Entity</b></p>
 * Resource. Represents the nodes in the graph.
*/
public record Resource(

    long id,
    String name,
    String description,
    String data_hash,
    int creation_time,
    int last_updated,
    Blob data,

    // FKs...
    short data_type_id,
    int user_id

) {}
