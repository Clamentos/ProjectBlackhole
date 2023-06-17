package io.github.clamentos.blackhole.persistence.entities;

import java.util.ArrayList;

import io.github.clamentos.blackhole.persistence.EntityMapper;
import io.github.clamentos.blackhole.web.dtos.Streamable;

public record Role(

    Short id,
    String name

) implements Streamable {

    @Override
    public byte[] toBytes() {

        ArrayList<Byte> temp = new ArrayList<>();

        if(id != null) temp.addAll(EntityMapper.numToBytes(id, 2));
        if(name != null) temp.addAll(EntityMapper.stringToList(name));

        return(EntityMapper.listToArray(temp));
    }
}
