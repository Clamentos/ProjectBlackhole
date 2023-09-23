package io.github.clamentos.blackhole.persistence.models;

import io.github.clamentos.blackhole.exceptions.Failures;
///
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
 * <p><b>Immutable data.</b></p>
 * <p>Tag resource entity.</p>
 * This class represents the "Tag" entity in the database.
*/
public final record TagEntity(

    ///
    int id,
    String name,
    int creation_date

    ///
) implements Projectable, Entity {

    @Override
    public List<DataEntry> reduce() {

        return(project(0b0111));
    }

    @Override
    public List<DataEntry> project(long fields) {

        List<DataEntry> entries = new ArrayList<>();

        entries.add((fields & 0b0001) > 0 ? new DataEntry(Types.INT, id) : null);
        entries.add((fields & 0b0010) > 0 ? new DataEntry(Types.STRING, name) : null);
        entries.add((fields & 0b0100) > 0 ? new DataEntry(Types.INT, creation_date) : null);

        return(entries);
    }

    ///
    public static List<TagEntity> newInstances(ResultSet result_set) throws PersistenceException {

        List<TagEntity> tags = new ArrayList<>();
        
        if(result_set == null) {

            throw new PersistenceException(Failures.NULL_DB_RESULT_SET);
        }

        try {

            while(result_set.next() == true) {

                tags.add(new TagEntity(result_set.getInt(0), result_set.getString(1), result_set.getInt(2)));
            }

            return(tags);
        }

        catch(SQLException exc) {

            throw new PersistenceException(exc);
        }
    }

    ///
}
