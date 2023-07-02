package io.github.clamentos.blackhole.web.dtos.actions;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import java.util.List;

//________________________________________________________________________________________________________________________________________

public record UserLogin(

    String username,
    String password

) {

    //____________________________________________________________________________________________________________________________________

    public static UserLogin deserialize(List<DataEntry> entries) {

        // TODO: implement
        return(null);
    }

    //____________________________________________________________________________________________________________________________________
}