package io.github.clamentos.blackhole.persistence.entities;

import io.github.clamentos.blackhole.common.framework.Streamable;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Type;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Entity</b></p>
 * Resource Tag, used to categorize resources.
*/
public record Tag(
    
    Integer id,
    String name,
    Integer creation_date

) implements Streamable {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public byte[] toBytes() {

        ArrayList<Byte> temp = new ArrayList<>();

        if(id != null) temp.addAll(Converter.numToBytes(id, 4));
        if(name != null) temp.addAll(Converter.stringToList(name));
        if(creation_date != null) temp.addAll(Converter.numToBytes(creation_date, 4));

        return(Converter.listToArray(temp));
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Deserialize the list of {@link DataEntry} in a list of {@link Tag}.
     * @param entries : The list of {@link DataEntry}.
     * @return The never null list of {@link Tag}.
     * @throw IllegalArgumentException If the input list is null, empty
     *        or if a {@link DataEntry} is not of type {@code Type.STRING}.
    */
    public static List<Tag> deserialize(List<DataEntry> entries) throws IllegalArgumentException {

        ArrayList<Tag> tags = new ArrayList<>();

        if(entries == null || entries.size() == 0) {

            throw new IllegalArgumentException("Tag name list cannot be null nor empty");
        }

        for(DataEntry entry : entries) {

            if(entry.data_type().equals(Type.STRING) == false) {

                throw new IllegalArgumentException("Invalid type. Expected STRING, got: " + entry.data_type().toString());
            }

            tags.add(new Tag(
                
                null,
                new String(entry.data()),
                null
            ));
        }

        return(tags);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Maps the {@link ResultSet} to a list of {@link Tag}.
     * @param result : The {@link ResultSet} from the query.
     * @param columns : A checklist of the columns to consider. The positions of the bits
     *                  indicate the index of the column. The LSB is the first column.
     * @return The never null list of tags. If none was mapped, the list will be empty.
     * @throws SQLException If the mapping fails.
    */
    public static List<Tag> mapMany(ResultSet result, int columns) throws SQLException {

        ArrayList<Tag> tags = new ArrayList<>();
        Tag temp;

        while(true) {

            temp = mapSingle(result, columns);

            if(temp != null) {

                tags.add(temp);
            }

            else {

                break;
            }
        }

        return(tags);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Maps the {@link ResultSet} to a single {@link Tag}.
     * @param result : The {@link ResultSet} from the query.
     * @param columns : A checklist of the columns to consider. The positions of the bits
     *                  indicate the index of the column. The LSB is the first column.
     * @return The {@link Tag}, or {@code null} if there was no mapping.
     * @throws SQLException If the mapping fails.
    */
    public static Tag mapSingle(ResultSet result, int columns) throws SQLException {

        Tag tag = null;

        if(result.next() == true) {

            tag = new Tag(

                ((columns & 0b0001) > 0) ? result.getInt(0) : null,
                ((columns & 0b0010) > 0) ? result.getString(1) : null,
                ((columns & 0b0100) > 0) ? result.getInt(2) : null
            );
        }

        return(tag);
    }

    //____________________________________________________________________________________________________________________________________
}
