package io.github.clamentos.blackhole.web.dtos;

//________________________________________________________________________________________________________________________________________

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

        for(Reducible streamable : data) {

            temp.addAll(streamable.reduce());
        }

        stuff = new byte[temp.size()][];
        count = 0;

        for(int i = 0; i < stuff.length; i++) {

            stuff[i] = temp.get(i).stream();
            count += stuff[i].length;
        }

        bytes = new byte[count + 1];
        System.arraycopy(response_status.stream(), 0, bytes, 0, 1);
        count = 1;

        for(int i = 0; i < stuff.length; i++) {

            System.arraycopy(stuff[i], 0, bytes, count, stuff[i].length);
            count += stuff[i].length;
        }

        return(bytes);
    }

    //____________________________________________________________________________________________________________________________________
}
