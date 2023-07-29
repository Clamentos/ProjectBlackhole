package io.github.clamentos.blackhole.network.request.components;

//________________________________________________________________________________________________________________________________________

/**
 * Simple enumeration containing all the possible protocol data types.
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
 *     <li>{@code BEGIN}: Specifies the beginning of an array. 1 byte size.</li>
 *     <li>{@code END}: Specifies the end of an array. 1 byte size.</li>
 * </ul>
*/
public enum Types {
    
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

    //____________________________________________________________________________________________________________________________________
}
