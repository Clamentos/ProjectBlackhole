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
 * This class corresponds to the <b>Relations</b> entity in the database.
 * The order of the fields must match the db schema.
 * <ol>
 *     <li>{@code Long source}: not null</li>
 *     <li>{@code Long name}: not null</li>
 * </ol>
*/
public record Relation(

    Long source,
    Long destination

) implements Reducible {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> result = new ArrayList<>();

        result.add(source == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.LONG, source));
        result.add(destination == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.LONG, destination));

        return(result);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Deserialize the list of {@link DataEntry} in a list of {@link Relation}.
     * @param entries : The list of {@link DataEntry}.
     * @return The never null list of {@link Relation}.
     * @throw IllegalArgumentException If the input list is null, empty
     *        or if a {@link DataEntry} is not of the correct type.
    */
    public static List<Relation> deserialize(List<DataEntry> entries) {

        ArrayList<Relation> relations = new ArrayList<>();
        int i;
        int fields;

        Long source;
        Long destination;

        if(entries == null || entries.size() == 0) {

            throw new IllegalArgumentException("Relation list cannot be null nor empty");
        }

        i = 0;

        while(i < entries.size()) {

            fields = Converter.entryToInt(entries.get(i));
            i++;

            source = null;
            destination = null;

            if((fields & 0b001) > 0) {source = Converter.entryToLong(entries.get(i)); i++;}
            if((fields & 0b010) > 0) {destination = Converter.entryToLong(entries.get(i)); i++;}

            relations.add(new Relation(

                source,
                destination
            ));
        }

        return(relations);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Maps the {@link ResultSet} to a list of {@link Relation}.
     * @param result : The {@link ResultSet} from the query.
     * @param columns : A checklist of the columns to consider. The positions of the bits
     *                  indicate the index of the column. The LSB is the first column.
     * @return The never null list of relations. If none was mapped, the list will be empty.
     * @throws SQLException If the mapping fails.
    */
    public static List<Relation> mapMany(ResultSet result, int columns) throws SQLException {

        ArrayList<Relation> relations = new ArrayList<>();
        Relation temp;

        while(true) {

            temp = mapSingle(result, columns);

            if(temp != null) {

                relations.add(temp);
            }

            else {

                break;
            }
        }

        return(relations);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Maps the {@link ResultSet} to a single {@link Relation}.
     * @param result : The {@link ResultSet} from the query.
     * @param columns : A checklist of the columns to consider. The positions of the bits
     *                  indicate the index of the column. The LSB is the first column.
     * @return The {@link Relation}, or {@code null} if there was no mapping.
     * @throws SQLException If the mapping fails.
    */
    public static Relation mapSingle(ResultSet result, int columns) throws SQLException {

        Relation relation = null;

        if(result.next() == true) {

            relation = new Relation(

                ((columns & 0b001) > 0) ? result.getLong(0) : null,
                ((columns & 0b010) > 0) ? result.getLong(1) : null
            );
        }

        return(relation);
    }

    //____________________________________________________________________________________________________________________________________
}
