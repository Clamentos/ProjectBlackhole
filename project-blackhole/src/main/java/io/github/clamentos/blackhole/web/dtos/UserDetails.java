package io.github.clamentos.blackhole.web.dtos;

import io.github.clamentos.blackhole.common.framework.Reducible;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Type;

import java.util.List;

public record UserDetails(

    byte[] session_id

) implements Reducible {

    @Override
    public List<DataEntry> reduce() {

        return(List.of(new DataEntry(Type.RAW, session_id)));
    }
}
