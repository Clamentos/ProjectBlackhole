package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Query parameter and data type bundle</h3>
 * This class holds the data and its associated data type for SQL query parameters.
 * @apiNote This class is <b>immutable data</b>.
*/
public record QueryParameter(

    Object parameter,
    SqlTypes type

    //____________________________________________________________________________________________________________________________________

) {}
