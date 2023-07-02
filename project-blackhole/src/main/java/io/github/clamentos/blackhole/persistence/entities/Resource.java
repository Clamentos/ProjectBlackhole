package io.github.clamentos.blackhole.persistence.entities;

import io.github.clamentos.blackhole.common.framework.Reducible;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Type;

import java.sql.Blob;

import java.util.ArrayList;
import java.util.List;

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
    Byte visibility,
    String data_hash,
    Byte datatype,
    Blob data,

    // FKs...
    Integer owner_user_id,
    Integer basic_category_id

) implements Reducible {

    // TODO: finish...

    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> result = new ArrayList<>();

        result.add(id == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, id));
        result.add(name == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.STRING, name));
        result.add(description == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.STRING, description));
        result.add(creation_date == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, creation_date));
        result.add(last_updated == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, last_updated));
        result.add(visibility == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.BYTE, visibility));
        result.add(data_hash == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.STRING, data_hash));
        result.add(datatype == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.BYTE, data_hash));
        //blob...
        result.add(owner_user_id == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, owner_user_id));
        result.add(basic_category_id == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, basic_category_id));

        return(result);
    }
}
