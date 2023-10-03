package io.github.clamentos.blackhole.utility;

///
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Types;

import java.util.ArrayList;
import java.util.List;

///
/**
 * <h3>Array utilities</h3>
 * 
 * This class offers static utility methods to perform a variety of operations on
 * arrays, such as, transforming a {@code List<DataEntry>} into a list of the desired type.
*/
public class ArrayUtils {
    
    ///
    public static void writeInteger(byte[] destination, int source, int start) {

        for(int i = 0; i < 4; i++) {

            destination[i + start] = (byte)(source & (0xFF000000 >> i));
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> parseIntegerArray(List<DataEntry> data, int[] start, boolean nullable) throws IllegalArgumentException {

        return((List<Integer>)parseArray(data, start, nullable, null, Types.INT));
    }

    @SuppressWarnings("unchecked")
    public static List<String> parseStringArray(List<DataEntry> data, int[] start, boolean nullable, String pattern) throws IllegalArgumentException {

        return((List<String>)parseArray(data, start, nullable, pattern, Types.STRING));
    }

    // If successfull, the List<?> is safe to cast to a list of:
    // Byte, Short, Integer, Long, Float, Double, String or Byte[]
    // depending on the given desired type

    // also updates the index to point to the END DataEntry of the list
    public static List<?> parseArray(List<DataEntry> data, int[] start, boolean nullable, String pattern, Types desired_type) throws IllegalArgumentException {

        List<Object> result;
        int idx = start[0];

        if(data.get(idx).data_type().equals(Types.NULL)) {

            return(null);
        }

        if(data.get(idx).data_type().equals(Types.BEGIN)) {

            result = new ArrayList<>();
            idx++;

            while(!data.get(idx).data_type().equals(Types.END)) {
                
                switch(desired_type) {

                    case BYTE: result.add(data.get(idx).entryAsByte(nullable)); break;
                    case SHORT: result.add(data.get(idx).entryAsShort(nullable)); break;
                    case INT: result.add(data.get(idx).entryAsInteger(nullable)); break;
                    case LONG: result.add(data.get(idx).entryAsLong(nullable)); break;
                    case FLOAT: result.add(data.get(idx).entryAsFloat(nullable)); break;
                    case DOUBLE: result.add(data.get(idx).entryAsDouble(nullable)); break;
                    case STRING: result.add(data.get(idx).entryAsString(pattern, nullable)); break;
                    case RAW: result.add(data.get(idx).entryAsRaw(nullable)); break;
                    
                    default: throw new IllegalArgumentException("The desired_type argument must be one of the following: BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, STRING or RAW");
                }

                idx++;
            }

            return(result);
        }

        throw new IllegalArgumentException("Expected NULL or BEGIN entry type, got: " + data.get(idx).data_type().name());
    }

    ///
}
