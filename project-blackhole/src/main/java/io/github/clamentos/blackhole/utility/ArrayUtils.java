package io.github.clamentos.blackhole.utility;

import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Types;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

    public static void writeInteger(byte[] destination, int source, int start) {

        for(int i = 0; i < 4; i++) {

            destination[i + start] = (byte)(source & (0xFF000000 >> i));
        }
    }
    
    /** true if null, false if empty, exception otherwise */
    public static boolean checkIfNullOrEmpty(List<DataEntry> data_entries, int start) throws IllegalStateException {

        if(data_entries.get(start).data_type().equals(Types.NULL)) {

            return(true);
        }

        if(data_entries.get(start).data_type().equals(Types.BEGIN) &&
           data_entries.get(start + 1).data_type().equals(Types.END)) {

            return(false);
        }

        throw new IllegalStateException();
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> makeIntegerArray(List<DataEntry> data_entries, int start, int end, boolean nullable) throws IllegalStateException {

        return((List<Integer>)(List<?>)makeArray(data_entries, start, end, nullable, Types.INT, null));
    }

    @SuppressWarnings("unchecked")
    public static List<String> makeStringArray(List<DataEntry> data_entries, int start, int end, boolean nullable, String regex) throws IllegalStateException {

        return((List<String>)(List<?>)makeArray(data_entries, start, end, nullable, Types.INT, regex));
    }

    //...

    private static List<Object> makeArray(List<DataEntry> data_entries, int start, int end, boolean nullable, Types types, String regex) throws IllegalStateException {

        int i;
        boolean early_stop = false;
        List<Object> result = new ArrayList<>();

        if(data_entries.get(start).data_type().equals(Types.BEGIN) == false) {

            throw new IllegalStateException();
        }

        for(i = start + 1; i < end - 1; i++) {

            if(data_entries.get(i).data_type().equals(Types.END)) {

                early_stop = true;
                break;
            }

            switch(types) {

                case BYTE: result.add(data_entries.get(i).entryAsByte(nullable));
                case SHORT: result.add(data_entries.get(i).entryAsShort(nullable));
                case INT: result.add(data_entries.get(i).entryAsInteger(nullable));
                case LONG: result.add(data_entries.get(i).entryAsLong(nullable));
                case FLOAT: result.add(data_entries.get(i).entryAsFloat(nullable));
                case DOUBLE: result.add(data_entries.get(i).entryAsDouble(nullable));
                case STRING: result.add(data_entries.get(i).entryAsString(regex, nullable));

                default: // TODO: this
            }
        }

        if(early_stop == false) { // Loop exited without finding END

            if(data_entries.get(i).data_type().equals(Types.END) == false) {

                throw new IllegalStateException();
            }
        }

        return(result);
    }
}
