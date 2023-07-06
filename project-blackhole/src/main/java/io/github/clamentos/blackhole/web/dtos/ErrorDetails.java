package io.github.clamentos.blackhole.web.dtos;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Reducible;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Type;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Simple class that holds the message and the timestamp of the error.</p>
*/
public record ErrorDetails(

    long timestamp,
    String message

) implements Reducible {

    //____________________________________________________________________________________________________________________________________

    public ErrorDetails(String message) {

        this(System.currentTimeMillis(), message);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * {@inheritDoc}
    */
    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> result = new ArrayList<>();

        result.add(new DataEntry(Type.LONG, timestamp));
        result.add(new DataEntry(Type.STRING, message));

        return(result);
    }

    //____________________________________________________________________________________________________________________________________
}
