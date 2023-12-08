package io.github.clamentos.blackhole.framework.implementation.network.transfer.components;

///
/**
 * <h3>Types</h3>
 * Enumeration containing all the possible data types for requests and responses.
 * <ul>
 *     <li>{@code BYTE}: 1 byte signed integer.</li>
 *     <li>{@code SHORT}: 2 byte signed integer.</li>
 *     <li>{@code INT}: 4 byte signed integer.</li>
 *     <li>{@code LONG}: 8 byte signed integer.</li>
 *     <li>{@code FLOAT}: 4 byte floating point number.</li>
 *     <li>{@code DOUBLE}: 8 byte floating point number.</li>
 *     <li>{@code STRING}: UTF-8 encoded string.</li>
 *     <li>{@code RAW}: Raw bynary data.</li>
 *     <li>{@code NULL}: Signifies a {@code null} value. 1 byte size.</li>
 *     <li>{@code BEGIN}: Specifies the beginning of an array (also used as a message control sequence). 1 byte size.</li>
 *     <li>{@code END}: Specifies the end of an array (also used as a message control sequence). 1 byte size.</li>
 * </ul>
*/
public enum Types {

    ///
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    STRING,
    RAW,
    NULL,
    BEGIN,
    END;

    ///
    /**
     * Instantiates a new {@link Types} object.
     * @param type_id : The id of the type.
     * @return The corresponding constant.
     * @throws IllegalArgumentException If {@code type_id} is not {@code 0}, {@code 1}, {@code 2}, {@code 3}, {@code 4},
     * {@code 5}, {@code 6}, {@code 7}, {@code 8}, {@code 9} or {@code 10}.
    */
    public static Types newInstance(byte type_id) throws IllegalArgumentException {

        switch(type_id) {

            case  0: return(Types.BYTE);
            case  1: return(Types.SHORT);
            case  2: return(Types.INT);
            case  3: return(Types.LONG);
            case  4: return(Types.FLOAT);
            case  5: return(Types.DOUBLE);
            case  6: return(Types.STRING);
            case  7: return(Types.RAW);
            case  8: return(Types.NULL);
            case  9: return(Types.BEGIN);
            case 10: return(Types.END);

            default: throw new IllegalArgumentException("(Types.newInstance) -> Unknown type: " + type_id);
        }
    }

    ///
}
