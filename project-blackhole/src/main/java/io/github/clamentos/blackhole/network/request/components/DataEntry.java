package io.github.clamentos.blackhole.network.request.components;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.framework.Streamable;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Immutable data.</b></p>
 * <p>Semi-structured data.</p>
 * Simple class to represent semi-structured data that can be sent over a stream.
*/
public final record DataEntry(

    Types data_type,
    Object data

) implements Streamable {

    //____________________________________________________________________________________________________________________________________
    
    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiates a new {@link DataEntry} object.
     * @param data : The raw data buffer.
     * @param offset : The starting position whithin the buffer.
     * @return The new {@link DataEntry}.
     * @throws IllegalArgumentException If an unknown {@link Types} is found.
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

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public byte[] stream() {

        byte[] bytes;
        byte[] result;

        switch(data_type) {

            case BYTE:

                bytes = numToBytes(data, 1);
                result = new byte[bytes.length + 1];
            
            break;

            case SHORT:

                bytes = numToBytes(data, 2);
                result = new byte[bytes.length + 1];
            
            break;

            case INT:

                bytes = numToBytes(data, 4);
                result = new byte[bytes.length + 1];
            
            break;

            case LONG:

                bytes = numToBytes(data, 8);
                result = new byte[bytes.length + 1];
            
            break;

            case FLOAT:

                bytes = numToBytes(data, 4);
                result = new byte[bytes.length + 1];
            
            break;

            case DOUBLE:

                bytes = numToBytes(data, 8);
                result = new byte[bytes.length + 1];
            
            break;

            case STRING:

                bytes = ((String)data).getBytes();
                result = new byte[bytes.length + 1];
            
            break;

            case RAW:

                bytes = (byte[])data;
                result = new byte[bytes.length + 1];
            
            break;

            case NULL:

                bytes = new byte[]{};
                result = new byte[bytes.length + 1];

            break;

            case BEGIN:

                bytes = new byte[]{};
                result = new byte[bytes.length + 1];

            break;

            case END:

                bytes = new byte[]{};
                result = new byte[bytes.length + 1];

            break;

            default: // Should never be executed... it's here so that the IDE doesn't complain...

                bytes = new byte[]{};
                result = new byte[bytes.length + 1];
            
            break;
        }

        result[0] = (byte)data_type.ordinal();
        System.arraycopy(bytes, 0, result, 1, bytes.length);

        return(bytes);
    }

    //____________________________________________________________________________________________________________________________________

    // Converts the number (passed as Object) into an array of bytes (Thread safe obviously).
    private byte[] numToBytes(Object data, int len) {

        byte[] result = new byte[len];
        long temp = (long)data;

        for(int i = 0; i < result.length; i++) {

            result[i] = (byte)(temp & (255 << i * 8) >> i * 8);
        }

        return(result);
    }

    // Converts raw bytes into a number (Thread safe obviously).
    private static long bytesToNum(byte[] data, int offset, int len) {

        long result = 0;

        for(int i = len - 1; i >= 0; i--) {

            result = result | (data[i + offset] << (len - 1 - i) * 8);
        }

        return(result);
    }

    //____________________________________________________________________________________________________________________________________
}
