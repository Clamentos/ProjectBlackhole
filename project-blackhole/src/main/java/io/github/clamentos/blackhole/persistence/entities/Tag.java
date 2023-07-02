package io.github.clamentos.blackhole.persistence.entities;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Reducible;
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
 * This class corresponds to the <b>Tags</b> entity in the database.
 * The order of the fields must match the db schema.
 * <ol>
 *     <li>{@code Integer id}: unique, not null</li>
 *     <li>{@code String name}: unique, not null, max 32 long</li>
 *     <li>{@code Integer creation_date}: not null</li>
 * </ol>
*/
public record Tag(
    
    Integer id,
    String name,
    Integer creation_date

) implements Reducible {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> result = new ArrayList<>();

        result.add(id == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, id));
        result.add(name == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.STRING, name));
        result.add(creation_date == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, creation_date));

        return(result);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Deserialize the list of {@link DataEntry} in a list of {@link Tag}.
     * @param entries : The list of {@link DataEntry}.
     * @return The never null list of {@link Tag}.
     * @throw IllegalArgumentException If the input list is null, empty
     *        or if a {@link DataEntry} is not of correct type.
    */
    public static List<Tag> deserialize(List<DataEntry> entries, int expected_fields) throws IllegalArgumentException {

        ArrayList<Tag> tags = new ArrayList<>();
        int i;
        int fields;

        Integer id;
        String name;
        Integer creation_date;

        if(entries == null || entries.size() == 0) {

            throw new IllegalArgumentException("Tag list cannot be null nor empty");
        }

        i = 0;

        while(i < entries.size()) {

            fields = Converter.entryToInt(entries.get(i));

            if(fields != expected_fields) {

                throw new IllegalArgumentException("Unexpected field list. Expected: " + expected_fields + ", got: " + fields);
            }

            i++;
            
            id = null;
            name = null;
            creation_date = null;

            if((fields & 0b000001) > 0) {id = Converter.entryToInt(entries.get(i)); i++;}
            if((fields & 0b000010) > 0) {name = Converter.entryToString(entries.get(i)); i++;}
            if((fields & 0b010000) > 0) {creation_date = Converter.entryToInt(entries.get(i)); i++;}

            tags.add(new Tag(

                id,
                name,
                creation_date
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
    public static List<Reducible> mapMany(ResultSet result, int columns) throws SQLException {

        ArrayList<Reducible> tags = new ArrayList<>();
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
