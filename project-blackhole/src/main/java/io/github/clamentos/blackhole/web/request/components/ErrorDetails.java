// OK
package io.github.clamentos.blackhole.web.request.components;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Reducible;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/** Simple class that holds the message and the timestamp of the error. */
public record ErrorDetails(

    long timestamp,
    String message

) implements Reducible {

    //____________________________________________________________________________________________________________________________________

    /**
     *<p><b>This method is thread safe.</p></b>
     * Instantiate a new {@link ErrorDetails} with the given message. 
     * @param message : The error message.
    */
    public ErrorDetails(String message) {

        this(System.currentTimeMillis(), message);
    }

    //____________________________________________________________________________________________________________________________________

    /** {@inheritDoc} */
    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> result = new ArrayList<>();

        result.add(new DataEntry(Types.LONG, timestamp));
        result.add(new DataEntry(Types.STRING, message));

        return(result);
    }

    //____________________________________________________________________________________________________________________________________
}