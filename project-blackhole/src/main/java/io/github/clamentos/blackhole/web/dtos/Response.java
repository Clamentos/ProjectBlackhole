package io.github.clamentos.blackhole.web.dtos;

import java.util.List;

public record Response(

    ResponseStatus response_status,
    List<DataEntry> data_entries

) implements Streamable {

    @Override
    public byte[] toBytes() {

        byte[] result;
        byte[][] temp;
        
        if(data_entries != null) {

            temp = new byte[data_entries.size()][];

            for(int i = 0; i < data_entries.size(); i++) {

                temp[i] = data_entries.get(i).toBytes();
            }

            result = new byte[(temp.length * temp[0].length) + 1];
            result[0] = response_status.toBytes()[0];

            for(int i = 0; i < result.length; i++) {

                System.arraycopy(temp[i], 0, result, (i * temp[i].length) + 1, temp[i].length);
            }
        }

        else {

            result = new byte[]{response_status.toBytes()[0]};
        }

        return(result);
    }
}
