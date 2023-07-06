package io.github.clamentos.blackhole.web.dtos;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Method;
import io.github.clamentos.blackhole.web.dtos.components.Entities;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Request class.</p>
 * <p>This class holds all the fields and data required to handle a request.</p>
 * The getter methods are all thread safe and standard.
*/
public record Request(

    Entities resource,
    Method method,
    byte[] session_id,
    List<DataEntry> data

) {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiates a new {@link Request} object.
     * @param data : The input data array, usually taken from a stream.
     * @throws IllegalArgumentException If the data holds any illegal value.
     * @throws ArrayIndexOutOfBoundsException If the data is incomplete or badly formatted.
    */
    public static Request deserialize(byte[] data) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {

        Entities resource;
        Method method;
        byte[] session_id;
        List<DataEntry> stuff;

        // passed to the "DataEntry.deserialize()" so that the position can be updated.
        // This is necessary because DataEntry size is only known at runtime and the method only returns a DataEntry,
        // which has no length field. "start_pos" array ALWAYS contains 1 element (similar to a "pointer" to the value).
        int[] start_pos;

        switch(data[0]) {

            case 0: resource = Entities.SYSTEM; break;
            case 1: resource = Entities.USER; break;
            case 2: resource = Entities.TAG; break;
            case 3: resource = Entities.RESOURCE; break;
            case 4: resource = Entities.ECHO; break;

            default: throw new IllegalArgumentException("Unknown resource type");
        }

        switch(data[1]) {

            case 0: method = Method.CREATE; break;
            case 1: method = Method.READ; break;
            case 2: method = Method.UPDATE; break;
            case 3: method = Method.DELETE; break;
            case 4: method = Method.LOGIN; break;
            case 5: method = Method.LOGOUT; break;

            default: throw new IllegalArgumentException("Unknown request method");
        }

        start_pos = new int[1];

        if(method != Method.LOGIN) {

            session_id = new byte[32];

            for(int i = 0; i < session_id.length; i++) {

                session_id[i] = data[i + 2];
            }

            start_pos[0] = 34;
        }

        else {

            session_id = null;
            start_pos[0] = 2;
        }

        stuff = new ArrayList<>();

        while(start_pos[0] < data.length) {

            stuff.add(DataEntry.deserialize(data, start_pos));
        }

        return(new Request(resource, method, session_id, stuff));
    }

    //____________________________________________________________________________________________________________________________________
}