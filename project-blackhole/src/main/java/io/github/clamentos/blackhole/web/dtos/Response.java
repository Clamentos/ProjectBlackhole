package io.github.clamentos.blackhole.web.dtos;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.exceptions.ErrorWrapper;
import io.github.clamentos.blackhole.common.framework.Reducible;
import io.github.clamentos.blackhole.common.framework.Streamable;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Response class.</p>
 * This class holds all the fields and data that can be sent through a stream.
*/
public record Response(

    ResponseStatus response_status,
    List<Reducible> data

) implements Streamable {

    //____________________________________________________________________________________________________________________________________

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
        System.arraycopy(response_status.stream(), 0, bytes, 4, 1);
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

    public static Response create(String error_message, Throwable error) {

        ArrayList<Reducible> error_details;
        ErrorWrapper wrapper;
        ResponseStatus status;

        error_details = new ArrayList<>();
        error_details.add(new ErrorDetails(error_message));

        if(error != null && error instanceof ErrorWrapper) {

            wrapper = (ErrorWrapper)error;

            switch(wrapper.getError()) {

                case SESSION_NOT_FOUND: status = ResponseStatus.UNAUTHENTICATED; break;
                case SESSION_EXPIRED: status = ResponseStatus.UNAUTHENTICATED; break;
                case NOT_ENOUGH_PRIVILEGES: status = ResponseStatus.DENIED; break;
                case BAD_FORMATTING: status = ResponseStatus.BAD_REQUEST; break;

                default: status = ResponseStatus.ERROR;
            }

            return(new Response(status, error_details));
        }

        return(new Response(ResponseStatus.ERROR, error_details));
    }

    //____________________________________________________________________________________________________________________________________
}
