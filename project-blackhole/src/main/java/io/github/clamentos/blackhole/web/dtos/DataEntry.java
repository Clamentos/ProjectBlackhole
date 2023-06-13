package io.github.clamentos.blackhole.web.dtos;

import java.util.ArrayList;
import java.util.List;

public record DataEntry(

    Type data_type,
    Integer length,      // for all types except Type.STRING and Type.RAW, this field must be null
    List<Byte> data

) implements Streamable {

    @Override
    public List<Byte> toBytes() {

        ArrayList<Byte> result = new ArrayList<>();

        result.addAll(data_type.toBytes());

        if(length != null) {

            result.add((byte)(length & 0x000000FF));
            result.add((byte)((length & 0x0000FF00) >> 8));
            result.add((byte)((length & 0x00FF0000) >> 16));
            result.add((byte)((length & 0xFF000000) >> 24));
        }

        result.addAll(data);
        return(result);
    }
}
