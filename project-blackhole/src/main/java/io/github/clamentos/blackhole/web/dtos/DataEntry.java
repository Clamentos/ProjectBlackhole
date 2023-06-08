package io.github.clamentos.blackhole.web.dtos;

import java.util.ArrayList;
import java.util.List;

public record DataEntry(

    Type data_type,
    Integer length,      // for all types except Type.STRING and Type.RAW, this field must be null
    byte[] data

) {

    public List<Byte> streamify() {

        ArrayList<Byte> result = new ArrayList<>();

        result.add(data_type.streamify());

        if(length != null) {

            result.add((byte)(length & 0x000000FF));
            result.add((byte)((length & 0x0000FF00) >> 8));
            result.add((byte)((length & 0x00FF0000) >> 16));
            result.add((byte)((length & 0xFF000000) >> 24));
        }

        for(Byte elem : data) {

            result.add(elem);
        }

        return(result);
    }
}
