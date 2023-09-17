package io.github.clamentos.blackhole.network.transfer;

///
import io.github.clamentos.blackhole.exceptions.FailuresWrapper;
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.ErrorDetails;
import io.github.clamentos.blackhole.network.transfer.components.ResponseStatuses;
import io.github.clamentos.blackhole.scaffolding.Reducible;
import io.github.clamentos.blackhole.scaffolding.Streamable;
import io.github.clamentos.blackhole.utility.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

///
/**
 * <h3>Network response object</h3>
 * 
 * This class holds all the fields and data that can be sent through a stream. The actual response that is
 * sent over sockets, has an extra 4 bytes at the beginning to specify the length of the message
 * (this class doesn't account for that).
 * 
 * @apiNote This class is <b>immutable data</b>.
*/
public final record Response(

    ResponseStatuses response_status,
    int remaining_requests,
    Reducible data

) implements Streamable {

    ///
    /**
     * Instantiates a new error {@link Response} object.
     * @param cause : The error cause.
     * @param remaining_requests : The number of remaining requests for the client.
     * @param message : The error message.
     * @see {@link FailuresWrapper}
    */
    public Response(Throwable cause, int remaining_requests, String message) {

        this(decode(cause), remaining_requests, new ErrorDetails(message));
    }

    ///
    /** {@inheritDoc} */
    @Override
    public byte[] stream() {

        byte[] bytes;
        byte[][] raw_data;
        int accumulator;
        List<DataEntry> reduced_data;

        reduced_data = new ArrayList<>();

        if(data != null) {

            reduced_data = data.reduce();
        }

        raw_data = new byte[reduced_data.size()][];
        accumulator = 0;

        for(int i = 0; i < raw_data.length; i++) {

            raw_data[i] = reduced_data.get(i).stream();
            accumulator += raw_data[i].length;
        }

        bytes = new byte[accumulator + 9];
        ArrayUtils.writeInteger(bytes, accumulator, 0);
        ArrayUtils.writeInteger(bytes, remaining_requests, 5);
        bytes[4] = (byte)response_status.ordinal();

        accumulator = 0;
        
        for(int i = 0; i < raw_data.length; i++) {

            System.arraycopy(raw_data[i], 0, bytes, accumulator, raw_data[i].length);
            accumulator += raw_data[i].length;
        }

        for(int i = 0; i < bytes.length; i++) {

            System.out.println("DBG: " + bytes[i]);
        }

        return(bytes);
    }

    ///
    // TODO: actual decode
    private static ResponseStatuses decode(Throwable cause) {

        if(cause instanceof FailuresWrapper) {

            switch(((FailuresWrapper)cause).getFailure()) {

                default: return(ResponseStatuses.BAD_REQUEST);
            }
        }

        else {

            return(ResponseStatuses.BAD_REQUEST);
        }
    }

    ///
}
