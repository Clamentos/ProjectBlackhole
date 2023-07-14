package io.github.clamentos.blackhole.web.request.components;

//________________________________________________________________________________________________________________________________________

import java.util.ArrayList;
import java.util.List;

import io.github.clamentos.blackhole.common.framework.Reducible;

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

        result.add(new DataEntry(Types.LONG, timestamp));
        result.add(new DataEntry(Types.STRING, message));

        return(result);
    }

    //____________________________________________________________________________________________________________________________________
}