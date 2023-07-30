package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Enumeration class.</b></p>
 * Simple enumeration containing all the possible accepted SQL data types for {@link QueryParameter}.
 * <ul>
 *     <li>{@code BYTE}: 1 byte signed integer.</li>
 *     <li>{@code SHORT}: 2 byte signed integer.</li>
 *     <li>{@code INT}: 4 byte signed integer.</li>
 *     <li>{@code LONG}: 8 byte signed integer.</li>
 *     <li>{@code FLOAT}: 4 byte floating point number.</li>
 *     <li>{@code DOUBLE}: 8 byte floating point number.</li>
 *     <li>{@code STRING}: UTF-8 encoded string.</li>
 *     <li>{@code BLOB}: Raw bynary data.</li>
 * </ul>
*/
public enum SqlTypes {
    
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    STRING,
    BLOB;

    //____________________________________________________________________________________________________________________________________
}
