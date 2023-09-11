package io.github.clamentos.blackhole.persistence.models;

///
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Types;
import io.github.clamentos.blackhole.scaffolding.Reducible;

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

    Short id,
    String name,
    Boolean is_complex

    ///
) implements Reducible {

    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> entries = new ArrayList<>();

        if(id != null) entries.add(new DataEntry(Types.SHORT, id));
        if(name != null) entries.add(new DataEntry(Types.STRING, name));
        if(is_complex != null) entries.add(new DataEntry(Types.BYTE, is_complex == true ? (byte)1 : (byte)0));

        return(entries);
    }

    ///
}
