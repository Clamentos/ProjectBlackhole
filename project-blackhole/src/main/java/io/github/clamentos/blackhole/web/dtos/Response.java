package io.github.clamentos.blackhole.web.dtos;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Streamable;
import io.github.clamentos.blackhole.web.dtos.components.ResponseStatus;

//________________________________________________________________________________________________________________________________________

/**
 * Response class.
 * This class holds all the fields and data that can be sent through a stream.
*/
public record Response(

    ResponseStatus response_status,
    Streamable data

) implements Streamable {

    //____________________________________________________________________________________________________________________________________

    @Override
    public byte[] toBytes() {

        byte[] status_bytes;
        byte[] data_bytes;
        byte[] result;

        status_bytes = response_status.toBytes();

        if(data != null) {

            data_bytes = data.toBytes();
            result = new byte[status_bytes.length + data_bytes.length];
            System.arraycopy(status_bytes, 0, result, 0, 1);
            System.arraycopy(data_bytes, 0, result, 1, data_bytes.length);
            
            return(result);
        }

        return(status_bytes);
    }

    //____________________________________________________________________________________________________________________________________
}
