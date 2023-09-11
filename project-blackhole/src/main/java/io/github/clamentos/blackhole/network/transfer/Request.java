package io.github.clamentos.blackhole.network.transfer;

///
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Methods;
import io.github.clamentos.blackhole.network.transfer.components.Resources;

import java.util.ArrayList;
import java.util.List;

///
/**
 * <h3>Network request object</h3>
 * This class holds all the fields and data required to handle a network request. The actual request that is
 * sent over sockets, has an extra 4 bytes at the beginning to specify the length of the message
 * (this class doesn't account for that).
 * @apiNote This class is <b>immutable data</b>.
*/
public final record Request(

    Resources resource,
    Methods method,
    byte[] session_id,
    List<DataEntry> data

) {

    ///
    /**
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

    ///
}
// TODO: equal & hashcode