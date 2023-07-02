package io.github.clamentos.blackhole.common.utility;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Type;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________


/**
 * Static utility class to convert to and from some basic types.
*/
public class Converter {
    
    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Converts an integer to a list of bytes.
     * @param number : The number to be converted.
     * @param length : The length (in bytes) of the number.
     * @return The never null list of bytes. If {@code length}
     *         is less or equal to 0, the list will be empty.
    */
    public static List<Byte> numToBytes(long number, int length) {

        List<Byte> result = new ArrayList<>();
        
        for(int i = 0; i < length; i++) {

            result.add((byte)(number & (255 << i * 8) >> i * 8));
        }

        return(result);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Converts an array of bytes to an integer.
     * @param data : The data to be converted.
     * @param start_pos : The starting position of the array.
     * @return The integer representing the data array.
     * @throw IllegalArgumentException If the size is not between 1 and 8.
    */
    public static long bytesToNum(byte[] data, int start_pos, int size) throws IllegalArgumentException {

        long result = 0;

        if(size < 1 || size > 8) {

            throw new IllegalArgumentException("Size must be between 1 and 8, passed: " + size);
        }

        for(int i = start_pos; i < start_pos + size; i++) {

            result = result | (data[i] << i * 8);
        }

        return(result);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Converts a list of bytes to an array of bytes.
     * @param list : The list to be converted.
     * @return The never null byte array representing the list.
    */
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

    /**
     * <p><b>This method is thread safe.</b></p>
     * Converts an array of bytes to a list of bytes.
     * @param array : The array to be converted.
     * @return The never null list of bytes representing the array.
    */
    public static List<Byte> ArrayToList(byte[] array) {

        ArrayList<Byte> result = new ArrayList<>();

        if(array != null) {

            for(int i = 0; i < array.length; i++) {

                result.add(array[i]);
            }
        }

        return(result);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Converts a string to a list of bytes.
     * @param str : The string to be converted.
     * @return The list of bytes representing the string.
     *         If {@code str} is {@code null}, this method will return {@code null}.
    */
    public static List<Byte> stringToList(String str) {

        ArrayList<Byte> result;

        if(str == null) {

            return(null);
        }

        result = new ArrayList<>();

        for(Byte ch : str.getBytes()) {

            result.add(ch);
        }

        return(result);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Extracts the data from the passed {@link DataEntry} with type checks.
     * @param entry : The data entry used to extract the actual data.
     * @return The data represented as a single byte.
     * @throws IllegalArgumentException If the type of the entry is not {@link Type#BYTE}.
    */
    public static byte entryToByte(DataEntry entry) throws IllegalArgumentException {

        if(entry.data_type().equals(Type.BYTE) == true) return((byte)entry.data());
        throw new IllegalArgumentException("Unexpected type. Expected: BYTE, got: " + entry.data_type().toString());
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Extracts the data from the passed {@link DataEntry} with type checks.
     * @param entry : The data entry used to extract the actual data.
     * @return The data represented as a single Byte ({@code null} is allowed).
     * @throws IllegalArgumentException If the type of the entry is not
     *                                  {@link Type#BYTE} nor {@link Type#NULL}.
    */
    public static Byte entryToByteNullable(DataEntry entry) throws IllegalArgumentException {

        if(entry.data_type().equals(Type.NULL) == true) return(null);
        if(entry.data_type().equals(Type.BYTE) == true) return((byte)entry.data());

        throw new IllegalArgumentException("Unexpected type. Expected: BYTE or NULL, got: " + entry.data_type().toString());
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Extracts the data from the passed {@link DataEntry} with type checks.
     * @param entry : The data entry used to extract the actual data.
     * @return The data represented as a single int.
     * @throws IllegalArgumentException If the type of the entry is not {@link Type#INT}.
    */
    public static int entryToInt(DataEntry entry) throws IllegalArgumentException {

        if(entry.data_type().equals(Type.INT) == true) return((int)entry.data());
        throw new IllegalArgumentException("Unexpected type. Expected: INT, got: " + entry.data_type().toString());
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Extracts the data from the passed {@link DataEntry} with type checks.
     * @param entry : The data entry used to extract the actual data.
     * @return The data represented as a single Integer ({@code null} is allowed).
     * @throws IllegalArgumentException If the type of the entry is not
     *                                  {@link Type#INT} nor {@link Type#NULL}.
    */
    public static Integer entryToIntNullable(DataEntry entry) throws IllegalArgumentException {

        if(entry.data_type().equals(Type.NULL) == true) return(null);
        if(entry.data_type().equals(Type.INT) == true) return((int)entry.data());

        throw new IllegalArgumentException("Unexpected type. Expected: INT or NULL, got: " + entry.data_type().toString());
    }



    /**
     * <p><b>This method is thread safe.</b></p>
     * Extracts the data from the passed {@link DataEntry} with type checks.
     * @param entry : The data entry used to extract the actual data.
     * @return The data represented as a single long.
     * @throws IllegalArgumentException If the type of the entry is not {@link Type#LONG}.
    */
    public static long entryToLong(DataEntry entry) throws IllegalArgumentException {

        if(entry.data_type().equals(Type.LONG) == true) return((long)entry.data());
        throw new IllegalArgumentException("Unexpected type. Expected: LONG, got: " + entry.data_type().toString());
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Extracts the data from the passed {@link DataEntry} with type checks.
     * @param entry : The data entry used to extract the actual data.
     * @return The data represented as a single Long ({@code null} is allowed).
     * @throws IllegalArgumentException If the type of the entry is not
     *                                  {@link Type#LONG} nor {@link Type#NULL}.
    */
    public static Long entryToLongNullable(DataEntry entry) throws IllegalArgumentException {

        if(entry.data_type().equals(Type.NULL) == true) return(null);
        if(entry.data_type().equals(Type.LONG) == true) return((long)entry.data());

        throw new IllegalArgumentException("Unexpected type. Expected: LONG or NULL, got: " + entry.data_type().toString());
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Extracts the data from the passed {@link DataEntry} with type checks.
     * @param entry : The data entry used to extract the actual data.
     * @return The data represented as a String ({@code null} is allowed).
     * @throws IllegalArgumentException If the type of the entry is not
     *                                  {@link Type#STRING} nor {@link Type#NULL}.
    */
    public static String entryToString(DataEntry entry) throws IllegalArgumentException {

        if(entry.data_type().equals(Type.NULL) == true) return(null);
        if(entry.data_type().equals(Type.STRING) == true) return((String)entry.data());

        throw new IllegalArgumentException("Unexpected type. Expected: STRING or NULL, got: " + entry.data_type().toString());
    }

    //____________________________________________________________________________________________________________________________________
}
