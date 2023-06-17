package io.github.clamentos.blackhole.persistence.entities;

import java.util.ArrayList;

import io.github.clamentos.blackhole.persistence.EntityMapper;
import io.github.clamentos.blackhole.web.dtos.Streamable;

/**
 * <p><b>Entity</b></p>
 * Join table between two {@link Resource}.
 * This entity represents the edges in the graph.
*/
public record Edge(

    Integer source,
    Integer destination,
    String data,
    Integer creation_time,
    Integer last_updated

) implements Streamable {

    @Override
    public byte[] toBytes() {

        ArrayList<Byte> temp = new ArrayList<>();

        if(source != null) temp.addAll(EntityMapper.numToBytes(source, 4));
        if(destination != null) temp.addAll(EntityMapper.numToBytes(destination, 4));
        if(data != null) temp.addAll(EntityMapper.stringToList(data));
        if(creation_time != null) temp.addAll(EntityMapper.numToBytes(creation_time, 4));
        if(last_updated != null) temp.addAll(EntityMapper.numToBytes(last_updated, 4));

        return(EntityMapper.listToArray(temp));
    }
}
