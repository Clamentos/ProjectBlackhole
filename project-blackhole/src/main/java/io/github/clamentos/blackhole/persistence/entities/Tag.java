package io.github.clamentos.blackhole.persistence.entities;

import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.web.dtos.Streamable;

import java.util.ArrayList;

/**
 * <p><b>Entity</b></p>
 * Resource Tag, used to categorize resources.
*/
public record Tag(
    
    Integer id,
    String name,
    Integer creation_date

) implements Streamable {

    @Override
    public byte[] toBytes() {

        ArrayList<Byte> temp = new ArrayList<>();

        if(id != null) temp.addAll(Converter.numToBytes(id, 4));
        if(name != null) temp.addAll(Converter.stringToList(name));
        if(creation_date != null) temp.addAll(Converter.numToBytes(creation_date, 4));

        return(Converter.listToArray(temp));
    }
}
