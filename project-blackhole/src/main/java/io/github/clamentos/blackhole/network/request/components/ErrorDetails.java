package io.github.clamentos.blackhole.network.request.components;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.framework.Reducible;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Immutable data.</b></p>
 * <p>Semi-structured data.</p>
 * Simple class that holds the message and the timestamp of the error.
*/
public final record ErrorDetails(

    long timestamp,
    String message

) implements Reducible {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link ErrorDetails} object. 
     * @param message : The error message.
    */
    public ErrorDetails(String message) {

        this(System.currentTimeMillis(), message);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
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