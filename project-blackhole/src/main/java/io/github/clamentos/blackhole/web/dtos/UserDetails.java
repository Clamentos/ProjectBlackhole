package io.github.clamentos.blackhole.web.dtos;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Reducible;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Type;

import java.util.List;

//________________________________________________________________________________________________________________________________________

public record UserDetails(

    byte[] session_id

) implements Reducible {

    //____________________________________________________________________________________________________________________________________

    /**
     * {@inheritDoc}
    */
    @Override
    public List<DataEntry> reduce() {

        return(List.of(new DataEntry(Type.RAW, session_id)));
    }

    //____________________________________________________________________________________________________________________________________
}
