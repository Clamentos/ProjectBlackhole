package io.github.clamentos.blackhole.web.request;

//________________________________________________________________________________________________________________________________________

import java.util.ArrayList;
import java.util.List;

import io.github.clamentos.blackhole.web.request.components.DataEntry;
import io.github.clamentos.blackhole.web.request.components.Methods;
import io.github.clamentos.blackhole.web.request.components.Resources;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Request class.</p>
 * <p>This class holds all the fields and data required to handle a request.</p>
 * The getter methods are all thread safe and standard.
*/
public record Request(

    Resources resource,
    Methods method,
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

        Resources resource;
        Methods method;
        byte[] session_id;
        List<DataEntry> stuff;

        // passed to the "DataEntry.deserialize()" so that the position can be updated.
        // This is necessary because DataEntry size is only known at runtime and the method only returns a DataEntry,
        // which has no length field. "start_pos" array ALWAYS contains 1 element (similar to a "pointer" to the value).
        int[] start_pos;

        resource = Resources.construct(data[0]);
        method = Methods.construct(data[1]);
        start_pos = new int[1];

        if(method != Methods.LOGIN) {

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