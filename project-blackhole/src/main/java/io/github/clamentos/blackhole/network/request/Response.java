package io.github.clamentos.blackhole.network.request;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.exceptions.Failure;
import io.github.clamentos.blackhole.framework.Reducible;
import io.github.clamentos.blackhole.framework.Streamable;
import io.github.clamentos.blackhole.network.request.components.DataEntry;
import io.github.clamentos.blackhole.network.request.components.ErrorDetails;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Immutable data.</b></p>
 * <p>Network response object.</p>
 * <p>This class holds all the fields and data that can be sent through a stream.</p>
 * The getter methods are all thread safe and standard.
*/
public final record Response(

    byte response_status,
    List<Reducible> data

) implements Streamable {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiates a new error {@link Response}.
     * @param error : The error itself.
     * @param error_message : The error message text.
     * @return The created error response.
    */
    public Response(Failure error, String error_message) {

        this((byte)error.getError().ordinal(), List.of(new ErrorDetails(error_message)));
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiates a new successfull {@link Response}. The response status code for success will always be 255.
     * @param data : The data to return.
     * @return The created response.
    */
    public Response(List<Reducible> data) {

        this((byte)255, data);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
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
        System.arraycopy(new byte[]{response_status}, 0, bytes, 4, 1);
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
}
