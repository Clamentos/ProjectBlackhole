package io.github.clamentos.blackhole.persistence.entities;

import java.sql.Blob;

/**
 * <p><b>Entity</b></p>
 * Resource. Represents the nodes in the graph.
*/
public record Resource(

    Long id,
    String name,
    String description,
    String data_hash,
    Integer creation_time,
    Integer last_updated,
    Blob data,

    // FKs...
    Short data_type_id,
    Integer user_id

) {}
