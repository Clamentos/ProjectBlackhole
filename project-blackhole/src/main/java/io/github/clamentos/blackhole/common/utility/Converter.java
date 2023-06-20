package io.github.clamentos.blackhole.common.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to convert to and from some basic types
*/
public class Converter {
    
    //____________________________________________________________________________________________________________________________________

    public static List<Byte> numToBytes(long number, int length) {

        List<Byte> result = new ArrayList<>();
        
        for(int i = 0; i < length; i++) {

            result.add((byte)(number & (255 << i * 8) >> i * 8));
        }

        return(result);
    }

    public static long bytesToNum(byte[] data) {

        long result = 0;

        for(int i = 0; i < data.length; i++) {

            result = result | (data[i] << i * 8);
        }

        return(result);
    }

    public static byte[] listToArray(List<Byte> list) {

        byte[] result;

        if(list == null) {

            return(new byte[0]);
        }

        result = new byte[list.size()];

        for(int i = 0; i < list.size(); i++) {

            result[i] = list.get(i);
        }

        return(result);
    }

    public static List<Byte> stringToList(String str) {

        ArrayList<Byte> result = new ArrayList<>();

        for(Byte ch : str.getBytes()) {

            result.add(ch);
        }

        return(result);
    }
}
