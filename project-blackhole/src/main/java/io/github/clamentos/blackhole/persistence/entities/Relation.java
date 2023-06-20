package io.github.clamentos.blackhole.persistence.entities;

import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.web.dtos.Streamable;

import java.util.ArrayList;

/**
 * <p><b>Entity</b></p>
 * Join table between two {@link Resource}.
 * This entity represents the edges in the graph.
*/
public record Relation(

    Integer source,
    Integer destination

) implements Streamable {

    @Override
    public byte[] toBytes() {

        ArrayList<Byte> temp = new ArrayList<>();

        if(source != null) temp.addAll(Converter.numToBytes(source, 4));
        if(destination != null) temp.addAll(Converter.numToBytes(destination, 4));

        return(Converter.listToArray(temp));
    }
}
