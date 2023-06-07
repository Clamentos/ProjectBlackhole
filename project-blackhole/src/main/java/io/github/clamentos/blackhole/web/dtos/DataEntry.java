package io.github.clamentos.blackhole.web.dtos;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public record DataEntry(

    Type data_type,
    Integer length,      // for all types except Type.STRING and Type.RAW, this field must be null
    byte[] data

) {

    // TODO: finish
    public byte[] streamify() {

        ArrayList<Byte> temp = new ArrayList<>();
        byte[] int_buffer;
        byte type = data_type.streamify();

        temp.add(data_type.streamify());

        if(data_type != null) {

            int_buffer = ByteBuffer.allocate(4).putInt(length).array();
            temp.add(int_buffer[0]);
            temp.add(int_buffer[1]);
            temp.add(int_buffer[2]);
            temp.add(int_buffer[3]);
        }
    }
}
