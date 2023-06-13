package io.github.clamentos.blackhole.web.dtos;

import java.util.ArrayList;
import java.util.List;

public record Response(

    ResponseStatus response_status,
    List<DataEntry> data_entries

) implements Streamable {

    @Override
    public List<Byte> toBytes() {

        ArrayList<Byte> result = new ArrayList<>();

        result.addAll(response_status.toBytes());

        for(DataEntry data : data_entries) {

            result.addAll(data.toBytes());
        }

        return(result);
    }
}
