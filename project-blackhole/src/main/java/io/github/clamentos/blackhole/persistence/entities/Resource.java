package io.github.clamentos.blackhole.persistence.entities;

import io.github.clamentos.blackhole.persistence.EntityMapper;
import io.github.clamentos.blackhole.web.dtos.Streamable;

import java.sql.Blob;
import java.util.ArrayList;

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

) implements Streamable {

    @Override
    public byte[] toBytes() {

        ArrayList<Byte> temp = new ArrayList<>();

        if(id != null) temp.addAll(EntityMapper.numToBytes(id, 8));
        if(name != null) temp.addAll(EntityMapper.stringToList(name));
        if(description != null) temp.addAll(EntityMapper.stringToList(description));
        if(data_hash != null) temp.addAll(EntityMapper.stringToList(data_hash));
        if(creation_time != null) temp.addAll(EntityMapper.numToBytes(creation_time, 4));
        if(last_updated != null) temp.addAll(EntityMapper.numToBytes(last_updated, 4));
        // TODO: blob streams...
        if(data_type_id != null) temp.addAll(EntityMapper.numToBytes(data_type_id, 2));
        if(user_id != null) temp.addAll(EntityMapper.numToBytes(user_id, 4));

        return(EntityMapper.listToArray(temp));
    }
}
