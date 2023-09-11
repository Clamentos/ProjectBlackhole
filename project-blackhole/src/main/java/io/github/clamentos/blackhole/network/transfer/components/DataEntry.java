package io.github.clamentos.blackhole.network.transfer.components;

///
import io.github.clamentos.blackhole.scaffolding.Streamable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

///
/**
 * <h3>Semi-structured request and response data holder</h3>
 * Simple record class to represent semi-structured data that can be sent over a stream.
 * @apiNote This class is <b>immutable data</b>.
*/
public final record DataEntry(

    Types data_type,
    Object data

) implements Streamable {

    ///
    /**
     * Instantiates a new {@link DataEntry} object.
     * @param data : The raw data buffer.
     * @param offset : The starting position whithin the buffer.
     * @return The new {@link DataEntry}.
     * @throws IllegalArgumentException If an unknown type is found.
     * @see {@link Types}
    */
    public static DataEntry deserialize(byte[] data, int[] offset) throws IllegalArgumentException {

        Types type;
        Object stuff;
        int pos;

        pos = offset[0];

        switch(data[pos]) {

            case 0:

                type = Types.BYTE;
                stuff = (byte)bytesToNum(data, pos + 1, 1);
                offset[0] += 2;

            break;

            case 1: 

                type = Types.SHORT;
                stuff = (short)bytesToNum(data, pos + 1, 2);
                offset[0] += 3;
                
            break;

            case 2: 
            
                type = Types.INT;
                stuff = (int)bytesToNum(data, pos + 1, 4);
                offset[0] += 5;
                
            break;

            case 3: 
            
                type = Types.LONG;
                stuff = (long)bytesToNum(data, pos + 1, 8);
                offset[0] += 9;
                
            break;

            case 4: 
            
                type = Types.FLOAT;
                stuff = Float.intBitsToFloat((int)bytesToNum(data, pos + 1, 4));
                offset[0] += 5;
                
            break;

            case 5: 
            
                type = Types.DOUBLE;
                stuff = Double.longBitsToDouble((long)bytesToNum(data, pos + 1, 8));
                offset[0] += 9;
                
            break;

            case 6:
            
                type = Types.STRING;
                stuff = new String(data, pos + 5, (int)bytesToNum(data, pos + 1, 4));
                offset[0] += ((String)stuff).length() + 5;
                
            break;

            case 7:
            
                type = Types.RAW;
                stuff = new byte[(int)bytesToNum(data, pos + 1, 4)];
                System.arraycopy(data, pos + 5, (byte[])stuff, 0, ((byte[])stuff).length);
                offset[0] += ((byte[])stuff).length + 5;
            
            break;

            case 8:
            
                type = Types.NULL;
                stuff = null;
                offset[0] += 1;
            
            break;

            case 9:
            
                type = Types.BEGIN;
                stuff = null;
                offset[0] += 1;
            
            break;

            case 10:
            
                type = Types.END;
                stuff = null;
                offset[0] += 1;
            
            break;

            default: throw new IllegalArgumentException("Unknown type: " + data[pos]);
        }

        return(new DataEntry(type, stuff));
    }

    ///
    /**
     * Converts {@code this} to a byte.
     * <p>The method also checks if the input conforms to the specified boundaries.</p>
     * @param nullable : Specifies if the null type is allowed.
     * @return The converted {@code this}.
     * @throws IllegalArgumentException If {@code this.data} was {@code null} and {@code nullable} was
     *                                  {@code false}.
     * @see {@link Types#NULL}
    */
    public Byte entryAsByte(boolean nullable) throws IllegalArgumentException {

        return((Byte)checker(data, nullable, Types.BYTE));
    }

    /**
     * Converts {@code this} to a short.
     * <p>The method also checks if the input conforms to the specified boundaries.</p>
     * @param nullable : Specifies if the null type is allowed.
     * @return The converted {@code this}.
     * @throws IllegalArgumentException If {@code this.data} was {@code null} and {@code nullable} was
     *                                  {@code false}.
     * @see {@link Types#NULL}
    */
    public Short entryAsShort(boolean nullable) throws IllegalArgumentException {

        return((Short)checker(data, nullable, Types.SHORT));
    }

    /**
     * Converts {@code this} to an int.
     * <p>The method also checks if the input conforms to the specified boundaries.</p>
     * @param nullable : Specifies if the null type is allowed.
     * @return The converted {@code this}.
     * @throws IllegalArgumentException If {@code this.data} was {@code null} and {@code nullable} was
     *                                  {@code false}.
     * @see {@link Types#NULL}
    */
    public Integer entryAsInteger(boolean nullable) throws IllegalArgumentException {

        return((Integer)checker(data, nullable, Types.INT));
    }

    /**
     * Converts {@code this} to a long.
     * <p>The method also checks if the input conforms to the specified boundaries.</p>
     * @param nullable : Specifies if the null type is allowed.
     * @return The converted {@code this}.
     * @throws IllegalArgumentException If {@code this.data} was {@code null} and {@code nullable} was
     *                                  {@code false}.
     * @see {@link Types#NULL}
    */
    public Long entryAsLong(boolean nullable) throws IllegalArgumentException {

        return((Long)checker(data, nullable, Types.LONG));
    }

    /**
     * Converts {@code this} to a float.
     * <p>The method also checks if the input conforms to the specified boundaries.</p>
     * @param nullable : Specifies if the null type is allowed.
     * @return The converted {@code this}.
     * @throws IllegalArgumentException If {@code this.data} was {@code null} and {@code nullable} was
     *                                  {@code false}.
     * @see {@link Types#NULL}
    */
    public Float entryAsFloat(boolean nullable) throws IllegalArgumentException {

        return((Float)checker(data, nullable, Types.FLOAT));
    }

    /**
     * Converts {@code this} to a double.
     * <p>The method also checks if the input conforms to the specified boundaries.</p>
     * @param nullable : Specifies if the null type is allowed.
     * @return The converted {@code this}.
     * @throws IllegalArgumentException If {@code this.data} was {@code null} and {@code nullable} was
     *                                  {@code false}.
     * @see {@link Types#NULL}
    */
    public Double entryAsDouble(boolean nullable) throws IllegalArgumentException {

        return((Double)checker(data, nullable, Types.DOUBLE));
    }

    /**
     * Converts {@code this} to a string.
     * <p>The method also checks if the input conforms to the specified boundaries.</p>
     * @param regex : The pattern used to check if {@code this} is acceptable.
     * @param nullable : Specifies if the null type is allowed.
     * @return The converted {@code this}.
     * @throws IllegalArgumentException If {@code regex} doesn't match or if {@code this.data} was
     *                                  {@code null} and {@code nullable} was {@code false}.
     * @throws PatternSyntaxException If {@code regex} is not a valid regular expression.
     * @see {@link Types#NULL}
    */
    public String entryAsString(String regex, boolean nullable) throws IllegalArgumentException, PatternSyntaxException {

        String str;

        if(data_type.equals(Types.NULL) == false) {

            if(data_type.equals(Types.STRING) == false) {

                throw new IllegalArgumentException("Input must be a STRING, got: " + data_type.name());
            }

            str = (String)data;
            
            if(Pattern.matches(regex, str) == true) {

                return(str);
            }

            throw new IllegalArgumentException("Input STRING doesn't conform");
        }

        if(nullable == false) {

            throw new IllegalArgumentException("Input STRING cannot be null");
        }

        return(null);
    }

    /**
     * Converts {@code this} to a byte array.
     * <p>The method also checks if the input conforms to the specified boundaries.</p>
     * @param nullable : Specifies if the null type is allowed.
     * @return The converted {@code this}.
     * @throws IllegalArgumentException If {@code this.data} was {@code null} and {@code nullable} was
     *                                  {@code false}.
     * @see {@link Types#NULL}
    */
    public byte[] entryAsRaw(boolean nullable) throws IllegalArgumentException {

        return((byte[])checker(data, nullable, Types.RAW));
    }

    ///
    /** {@inheritDoc} */
    @Override
    public byte[] stream() {

        byte[] bytes;
        byte[] result;

        switch(data_type) {

            case BYTE: bytes = numToBytes(data, 1); break;
            case SHORT: bytes = numToBytes(data, 2); break;
            case INT, FLOAT: bytes = numToBytes(data, 4); break;
            case LONG, DOUBLE: bytes = numToBytes(data, 8); break;
            case STRING: bytes = ((String)data).getBytes(); break;
            case RAW: bytes = (byte[])data; break;
            case NULL, BEGIN, END: bytes = new byte[]{}; break;

            // Should never be executed... it's here so that the IDE doesn't complain...
            default: bytes = new byte[]{}; break;
        }

        result = new byte[bytes.length + 1];
        result[0] = (byte)data_type.ordinal();
        System.arraycopy(bytes, 0, result, 1, bytes.length);

        return(bytes);
    }

    ///
    // Utility methods.

    // Converts the number (passed as Object) into an array of bytes.
    private byte[] numToBytes(Object data, int len) {

        byte[] result = new byte[len];
        long temp = (long)data;

        for(int i = 0; i < result.length; i++) {

            result[i] = (byte)(temp & (255 << i * 8) >> i * 8);
        }

        return(result);
    }

    // Converts raw bytes into a number.
    private static long bytesToNum(byte[] data, int offset, int len) {

        long result = 0;

        for(int i = len - 1; i >= 0; i--) {

            result = result | (data[i + offset] << (len - 1 - i) * 8);
        }

        return(result);
    }

    // Checks if the passed is conforming to the specfied characteristics.
    private <T> T checker(T object, boolean nullable, Types type) throws IllegalArgumentException {

        if(data_type.equals(Types.NULL) == false) {

            if(data_type.equals(type) == false) {

                throw new IllegalArgumentException(

                    "Input must be a " + type.name() + ", got: " + data_type.name()
                );
            }

            return(object);
        }

        if(nullable == false) {

            throw new IllegalArgumentException("Input " + type.name() + " cannot be null");
        }

        return(null);
    }

    ///
}
