package io.github.clamentos.blackhole.persistence.models;

import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Types;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.scaffolding.Reducible;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Immutable data.</b></p>
 * <p>Tag resource entity.</p>
 * <p>This class represents the "tag" entity in the database.</p>
 * The getter methods are all thread safe and standard.
*/
public final record TagEntity(

    Integer id,
    String name,
    Integer creation_date

    //____________________________________________________________________________________________________________________________________

) implements Reducible {

    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> entries = new ArrayList<>();

        if(id != null) entries.add(new DataEntry(Types.INT, id));
        if(name != null) entries.add(new DataEntry(Types.STRING, name));
        if(creation_date != null) entries.add(new DataEntry(Types.INT, creation_date));

        return(entries);
    }

    public static List<TagEntity> newInstances(ResultSet result_set) throws SQLException {

        List<TagEntity> tags = new ArrayList<>();

        while(result_set.next() == true) {

            tags.add(new TagEntity(result_set.getInt(0), result_set.getString(1), result_set.getInt(2)));
        }

        return(tags);
    }

    public static String getColumnNames(byte fields) {

        ArrayList<String> column_names = new ArrayList<>();
        String result;
        
        column_names.add("id");
        column_names.add("name");
        column_names.add("creation_date");
        result = column_names.toString();

        return(result.substring(1, result.length() - 1));
    }
}