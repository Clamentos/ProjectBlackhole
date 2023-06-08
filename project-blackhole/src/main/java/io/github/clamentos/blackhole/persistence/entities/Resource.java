package io.github.clamentos.blackhole.persistence.entities;

import java.sql.Blob;

public record Resource(

    long id,
    String name,
    String description,
    String data_hash,
    int creation_time,
    int last_updated,
    Blob data,

    // FKs...
    short blob_type_id,
    int user_id

) {}
