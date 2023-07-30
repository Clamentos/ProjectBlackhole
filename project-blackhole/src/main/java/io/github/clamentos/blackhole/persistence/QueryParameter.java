package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Immutable data.</b></p>
 * <p>Query parameter and data type bundle.</p>
 * <p>This class holds the data and its associated data type for SQL query parameters.</p>
 * The getter methods are all thread safe and standard.
*/
public record QueryParameter(

    Object parameter,
    SqlTypes type

    //____________________________________________________________________________________________________________________________________

) {}
