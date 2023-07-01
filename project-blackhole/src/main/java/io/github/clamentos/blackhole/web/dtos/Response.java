package io.github.clamentos.blackhole.web.dtos;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Streamable;
import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.web.dtos.components.ResponseStatus;

//________________________________________________________________________________________________________________________________________

/**
 * Response class.
 * This class holds all the fields and data that can be sent through a stream.
*/
public record Response(

    ResponseStatus response_status,
    List<Streamable> data

) implements Streamable {

    //____________________________________________________________________________________________________________________________________

    @Override
    public byte[] toBytes() {

        byte[] status_bytes;
        byte[] data_bytes;
        byte[] result;
        ArrayList<Byte> temp;

        status_bytes = response_status.toBytes();

        if(data != null && data.size() > 0) {

            temp = new ArrayList<>();

            for(Streamable s : data) {

                temp.addAll(Converter.ArrayToList(s.toBytes()));
            }

            data_bytes = Converter.listToArray(temp);
            result = new byte[status_bytes.length + data_bytes.length];
            System.arraycopy(status_bytes, 0, result, 0, 1);
            System.arraycopy(data_bytes, 0, result, 1, data_bytes.length);
            
            return(result);
        }

        return(status_bytes);
    }

    //____________________________________________________________________________________________________________________________________
}
