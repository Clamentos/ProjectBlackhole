package io.github.clamentos.blackhole.web.dtos.queries;

import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import java.util.List;

public record UserLogin(

    String username,
    String password

) {

    public static UserLogin deserialize(List<DataEntry> entries) {

        // TODO: implement
        return(null);
    }
}
