package io.github.clamentos.blackhole.persistence.entities;

import io.github.clamentos.blackhole.common.framework.Streamable;
import io.github.clamentos.blackhole.common.utility.Converter;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * <p><b>Entity</b></p>
 * This class corresponds to the <b>Resources</b> entity in the database.
 * The order of the fields must match the db schema.
 * <ol>
 *     <li>{@code Integer id}: unique, not null</li>
 *     <li>{@code String name}: unique, not null, max 128 long</li>
 *     <li>{@code String description}: not null, max 1024 long</li>
 *     <li>{@code Integer creation_date}: not null</li>
 *     <li>{@code Integer last_updated}: not null</li>
 *     <li>{@code Boolean visible}: not null</li>
 *     <li>{@code String data_hash}: not null, max 22 long</li>
 *     <li>{@code Byte datatype}: not null</li>
 *     <li>{@code Blob data}</li>
 *     <li>{@code Integer owner_user_id}: not null</li>
 *     <li>{@code Integer basic_category}: not null</li>
 * </ol>
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

    // TODO: finish...

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
        // blob streams...
        if(owner_user_id != null) temp.addAll(Converter.numToBytes(owner_user_id, 4));
        if(basic_category != null) temp.addAll(Converter.numToBytes(basic_category, 4));

        return(Converter.listToArray(temp));
    }
}
