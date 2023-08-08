package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Common SQL data types</h3>
 * 
 * Simple enumeration containing all the possible accepted SQL data types for the query parameters.
 * 
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
 * 
 * @see {@link QueryParameter}
 * @apiNote This class is an <b>enumeration</b>.
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
