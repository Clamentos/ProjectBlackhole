package io.github.clamentos.blackhole.persistence.entities;

import io.github.clamentos.blackhole.common.utility.Converter;
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
    Integer creation_date,
    Integer last_updated,
    Boolean visible,
    String data_hash,
    Byte datatype,
    Blob data,

    // FKs...
    Integer owner_user_id,
    Integer basic_category

) implements Streamable {

    @Override
    public byte[] toBytes() {

        ArrayList<Byte> temp = new ArrayList<>();

        if(id != null) temp.addAll(Converter.numToBytes(id, 8));
        if(name != null) temp.addAll(Converter.stringToList(name));
        if(description != null) temp.addAll(Converter.stringToList(description));
        if(creation_date != null) temp.addAll(Converter.numToBytes(creation_date, 4));
        if(last_updated != null) temp.addAll(Converter.numToBytes(last_updated, 4));
        if(visible != null) temp.add(visible ? (byte)1 : (byte)0);
        if(data_hash != null) temp.addAll(Converter.stringToList(data_hash));
        // TODO: blob streams...
        if(owner_user_id != null) temp.addAll(Converter.numToBytes(owner_user_id, 4));
        if(basic_category != null) temp.addAll(Converter.numToBytes(basic_category, 4));

        return(Converter.listToArray(temp));
    }
}
