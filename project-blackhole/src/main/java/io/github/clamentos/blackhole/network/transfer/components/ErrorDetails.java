package io.github.clamentos.blackhole.network.transfer.components;

///
// Some imports are only used for JavaDocs.

import io.github.clamentos.blackhole.network.transfer.Response;
import io.github.clamentos.blackhole.scaffolding.Reducible;

import java.util.ArrayList;
import java.util.List;

///
/**
 * <h3>Response error details holder</h3>
 * Simple record class that holds the message and the timestamp for error responses.
 * @see {@link Response}.
 * @apiNote This class is <b>immutable data.</b>
*/
public final record ErrorDetails(

    long timestamp,
    String message

) implements Reducible {

    ///
    /**
     * Instantiates a new {@link ErrorDetails} object. 
     * @param message : The error message.
    */
    public ErrorDetails(String message) {

        this(System.currentTimeMillis(), message);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> result = new ArrayList<>();

        result.add(new DataEntry(Types.LONG, timestamp));
        result.add(new DataEntry(Types.STRING, message));

        return(result);
    }

    ///
}
