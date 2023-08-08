package io.github.clamentos.blackhole.network.transfer;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.exceptions.FailuresWrapper;
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.ErrorDetails;
import io.github.clamentos.blackhole.network.transfer.components.ResponseStatuses;
import io.github.clamentos.blackhole.scaffolding.Reducible;
import io.github.clamentos.blackhole.scaffolding.Streamable;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Network response object</h3>
 * This class holds all the fields and data that can be sent through a stream. The actual response that is
 * sent over sockets, has an extra 4 bytes at the beginning to specify the length of the message
 * (this class doesn't account for that).
 * @apiNote This class is <b>immutable data</b>.
*/
public final record Response(

    ResponseStatuses response_status,
    List<Reducible> data

) implements Streamable {

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new error {@link Response} object.
     * @param cause : The error cause.
     * @param message : The error message.
     * @see {@link FailuresWrapper}
    */
    public Response(Throwable cause, String message) {

        this(decode(cause), List.of(new ErrorDetails(message)));
    }

    //____________________________________________________________________________________________________________________________________

    /** {@inheritDoc} */
    @Override
    public byte[] stream() {

        List<DataEntry> temp = new ArrayList<>();
        byte[][] stuff;
        byte[] bytes;
        int count;

        if(data != null) {

            for(Reducible streamable : data) {

                temp.addAll(streamable.reduce());
            }
        }

        stuff = new byte[temp.size()][];
        count = 0;

        for(int i = 0; i < stuff.length; i++) {

            stuff[i] = temp.get(i).stream();
            count += stuff[i].length;
        }

        bytes = new byte[count + 5];
        System.arraycopy(new byte[]{(byte)response_status.ordinal()}, 0, bytes, 4, 1);
        count = 1;

        for(int i = 0; i < stuff.length; i++) {

            System.arraycopy(stuff[i], 0, bytes, count, stuff[i].length);
            count += stuff[i].length;
        }

        count = bytes.length - 4;
        
        bytes[0] = (byte)(count & 0xFF000000);
        bytes[1] = (byte)(count & 0x00FF0000);
        bytes[2] = (byte)(count & 0x0000FF00);
        bytes[3] = (byte)(count & 0x000000FF);

        return(bytes);
    }

    //____________________________________________________________________________________________________________________________________

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

    //____________________________________________________________________________________________________________________________________
}
