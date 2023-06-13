package io.github.clamentos.blackhole.web.dtos;

public record DataEntry(

    Type data_type,
    Integer length,      // for all types except Type.STRING and Type.RAW, this field must be null
    byte[] data

) implements Streamable {

    @Override
    public byte[] toBytes() {

        byte[] result = new byte[5 + data.length];

        result[0] = data_type.toBytes()[0];

        if(length != null) {

            result[1] = (byte)(length & 0x000000FF);
            result[2] = (byte)((length & 0x0000FF00) >> 8);
            result[3] = (byte)((length & 0x00FF0000) >> 16);
            result[4] = (byte)((length & 0xFF000000) >> 24);
        }

        System.arraycopy(data, 0, result, 5, data.length);
        return(result);
    }
}
