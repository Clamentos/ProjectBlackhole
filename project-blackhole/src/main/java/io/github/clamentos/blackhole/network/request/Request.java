package io.github.clamentos.blackhole.network.request;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.network.request.components.DataEntry;
import io.github.clamentos.blackhole.network.request.components.Methods;
import io.github.clamentos.blackhole.network.request.components.Resources;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Immutable data.</b></p>
 * <p>Network request object.</p>
 * <p>This class holds all the fields and data required to handle a request.</p>
 * The getter methods are all thread safe and standard.
*/
public final record Request(

    Resources resource,
    Methods method,
    byte[] session_id,
    List<DataEntry> data

) {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Deserializes the given data byte array into a new {@link Request} object.
     * @param data : The input data array, usually taken from a stream.
     * @return The deserialized request object.
     * @throws IllegalArgumentException If {@code data} holds any illegal value.
     * @throws ArrayIndexOutOfBoundsException If {@code data} is incomplete or badly formatted.
    */
    public static Request deserialize(byte[] data) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {

        Resources resource;
        Methods method;
        byte[] session_id;
        List<DataEntry> stuff;

        // passed to the "DataEntry.deserialize()" so that the position can be updated.
        // This is necessary because DataEntry size is only known at runtime
        // and the method only returns a DataEntry, which has no length field.
        // "start_pos" array ALWAYS contains 1 element (similar to a "pointer" to the value).
        int[] start_pos;

        resource = Resources.newInstance(data[0]);
        method = Methods.newInstance(data[1]);
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