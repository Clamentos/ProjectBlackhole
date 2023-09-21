package io.github.clamentos.blackhole.persistence.models;

///
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Types;
import io.github.clamentos.blackhole.scaffolding.Projectable;

import java.util.ArrayList;
import java.util.List;

///
/**
 * <p><b>Immutable data.</b></p>
 * <p>Type resource entity.</p>
 * <p>This class represents the "type" entity in the database.</p>
 * The getter methods are all thread safe and standard.
*/
public record TypeEntity(

    ///
    short id,
    String name,
    boolean is_complex

    ///
) implements Projectable {

    @Override
    public List<DataEntry> reduce() {

        return(project(0b0111));
    }

    @Override
    public List<DataEntry> project(long fields) {

        List<DataEntry> entries = new ArrayList<>();

        entries.add((fields & 0b0001) > 0 ? new DataEntry(Types.INT, id) : null);
        entries.add((fields & 0b0010) > 0 ? new DataEntry(Types.STRING, name) : null);
        entries.add((fields & 0b0100) > 0 ? new DataEntry(Types.BYTE, is_complex == true ? (byte)1 : (byte)0) : null);

        return(entries);
    }

    ///
}
