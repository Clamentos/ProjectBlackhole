package io.github.clamentos.blackhole.persistence.models;

///
import io.github.clamentos.blackhole.exceptions.Failures;
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Types;
import io.github.clamentos.blackhole.persistence.PersistenceException;
import io.github.clamentos.blackhole.scaffolding.Entity;
import io.github.clamentos.blackhole.scaffolding.Projectable;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

///
/**
 * <h3>Tag entity</h3>
 * This class represents the "Tag" entity in the database.
*/
public final record TagEntity(

    ///
    int id,
    int creation_date,
    int last_modified,
    String name,
    byte domain

    ///
) implements Projectable, Entity {

    /** {@inheritDoc} */
    @Override
    public List<DataEntry> reduce() {

        return(project(0b011111));
    }

    /** {@inheritDoc} */
    @Override
    public List<DataEntry> project(long fields) {

        List<DataEntry> entries = new ArrayList<>();

        entries.add((fields & 0b000001) > 0 ? new DataEntry(Types.INT, id) : null);
        entries.add((fields & 0b000010) > 0 ? new DataEntry(Types.INT, creation_date) : null);
        entries.add((fields & 0b000100) > 0 ? new DataEntry(Types.INT, last_modified) : null);
        entries.add((fields & 0b001000) > 0 ? new DataEntry(Types.STRING, name) : null);
        entries.add((fields & 0b010000) > 0 ? new DataEntry(Types.BYTE, domain) : null);

        return(entries);
    }

    ///
    /**
     * Maps the {@code result_set} to a list of {@link TagEntity}.
     * 
     * @param result_set : The result set from the database.
     * @param start_column_index : The column index (starting from 0), from which to start the mapping
     *                             (useful when the result set is a join).
     * @return : The mapped list of {@link TagEntity}.
     * @throws PersistenceException If the {@code result_set} is {@code null} or a database error occurs.
     */
    public static List<TagEntity> newInstances(ResultSet result_set, int start_column_index) throws PersistenceException {

        List<TagEntity> tags = new ArrayList<>();
        
        if(result_set == null) {

            throw new PersistenceException(Failures.NULL_DB_RESULT_SET);
        }

        try {

            while(result_set.next() == true) {

                tags.add(new TagEntity(
                    
                    result_set.getInt(start_column_index + 1),
                    result_set.getInt(start_column_index + 2),
                    result_set.getInt(start_column_index + 3),
                    result_set.getString(start_column_index + 4),
                    (byte)result_set.getShort(start_column_index + 5)
                ));
            }

            return(tags);
        }

        catch(SQLException exc) {

            throw new PersistenceException(exc);
        }
    }

    ///
}
