package io.github.clamentos.blackhole.web.dtos;

import java.util.ArrayList;
import java.util.List;

public record Response(

    ResponseStatus response_status,
    DataEntry[] data_entries
) {

    public List<Byte> streamify() {

        ArrayList<Byte> result = new ArrayList<>();

        result.add(response_status.streamify());

        for(DataEntry data : data_entries) {

            result.addAll(data.streamify());
        }

        return(result);
    }
}
