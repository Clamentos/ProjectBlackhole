package io.github.clamentos.blackhole.web.dtos.components;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Streamable;

//________________________________________________________________________________________________________________________________________

/**
 * Simple class to represent semi-structured data.
*/
public record DataEntry(

    Type data_type,
    Object data

) implements Streamable {

    //____________________________________________________________________________________________________________________________________
    
    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiates a new {@link DataEntry} with the given raw data buffer.
     * @param data : The raw data buffer.
     * @param offset : Starting position of the buffer.
     * @return The new {@link DataEntry}.
     * @throws IllegalArgumentException If an unknown {@link Type} is found.
    */
    public static DataEntry deserialize(byte[] data, int[] offset) throws IllegalArgumentException {

        Type type;
        Object stuff;
        int pos;

        pos = offset[0];

        switch(data[pos]) {

            case 0:

                type = Type.BYTE;
                stuff = (byte)bytesToNum(data, pos + 1, 1);
                offset[0] += 2;

            break;

            case 1: 
            
                type = Type.SHORT;
                stuff = (short)bytesToNum(data, pos + 1, 2);
                offset[0] += 3;
                
            break;

            case 2: 
            
                type = Type.INT;
                stuff = (int)bytesToNum(data, pos + 1, 4);
                offset[0] += 5;
                
            break;

            case 3: 
            
                type = Type.LONG;
                stuff = (long)bytesToNum(data, pos + 1, 8);
                offset[0] += 9;
                
            break;

            case 4: 
            
                type = Type.FLOAT;
                stuff = Float.intBitsToFloat((int)bytesToNum(data, pos + 1, 4));
                offset[0] += 5;
                
            break;

            case 5: 
            
                type = Type.DOUBLE;
                stuff = Double.longBitsToDouble((long)bytesToNum(data, pos + 1, 8));
                offset[0] += 9;
                
            break;

            case 6:
            
                type = Type.STRING;
                stuff = new String(data, pos + 5, (int)bytesToNum(data, pos + 1, 4));
                offset[0] += ((String)stuff).length() + 5;
                
            break;

            case 7:
            
                type = Type.RAW;
                stuff = new byte[(int)bytesToNum(data, pos + 1, 4)];
                System.arraycopy(data, pos + 5, (byte[])stuff, 0, ((byte[])stuff).length);
                offset[0] += ((byte[])stuff).length + 5;
            
            break;

            case 8:
            
                type = Type.NULL;
                stuff = null;
                offset[0] += 1;
            
            break;

            default: throw new IllegalArgumentException("Unknown type: " + data[pos]);
        }

        return(new DataEntry(type, data));
    }

    //____________________________________________________________________________________________________________________________________

    /**
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
                result[0] = 0;
            
            break;

            case SHORT:

                bytes = numToBytes(data, 2);
                result = new byte[bytes.length + 1];
                result[0] = 1;
            
            break;

            case INT:

                bytes = numToBytes(data, 4);
                result = new byte[bytes.length + 1];
                result[0] = 2;
            
            break;

            case LONG:

                bytes = numToBytes(data, 8);
                result = new byte[bytes.length + 1];
                result[0] = 3;
            
            break;

            case FLOAT:

                bytes = numToBytes(data, 4);
                result = new byte[bytes.length + 1];
                result[0] = 4;
            
            break;

            case DOUBLE:

                bytes = numToBytes(data, 8);
                result = new byte[bytes.length + 1];
                result[0] = 5;
            
            break;

            case STRING:

                bytes = ((String)data).getBytes();
                result = new byte[bytes.length + 1];
                result[0] = 6;
            
            break;

            case RAW:

                bytes = (byte[])data;
                result = new byte[bytes.length + 1];
                result[0] = 7;
            
            break;

            default:

                bytes = new byte[]{};
                result = new byte[bytes.length + 1];
                result[0] = 8;
            
            break;
        }

        System.arraycopy(bytes, 0, result, 1, bytes.length);
        return(bytes);
    }

    //____________________________________________________________________________________________________________________________________

    private byte[] numToBytes(Object data, int len) {

        byte[] result = new byte[len];
        long temp = (long)data;

        for(int i = 0; i < result.length; i++) {

            result[i] = (byte)(temp & (255 << i * 8) >> i * 8);
        }

        return(result);
    }

    private static long bytesToNum(byte[] data, int offset, int len) {

        long result = 0;

        for(int i = 0; i < len; i++) {

            result = result | (data[i] << i * 8);
        }

        return(result);
    }

    //____________________________________________________________________________________________________________________________________
}