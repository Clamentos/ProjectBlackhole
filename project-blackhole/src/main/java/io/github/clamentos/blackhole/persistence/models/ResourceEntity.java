package io.github.clamentos.blackhole.persistence.models;

///
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.scaffolding.Projectable;

import java.sql.Blob;

import java.util.List;

///
public record ResourceEntity(

    ///
    long id,
    String name,
    int creation_date,
    int last_modified,
    boolean is_private,
    int upvotes,
    int downvotes,
    String data_hash,
    Blob data,
    short type_id,
    int owner_id,
    int updated_by_id

    ///
) implements Projectable {

    @Override
    public List<DataEntry> reduce() {

        return(project(0b0111111111111));
    }

    @Override
    public List<DataEntry> project(long fields) {

        // TODO
        return(null);
    }

    ///
}
