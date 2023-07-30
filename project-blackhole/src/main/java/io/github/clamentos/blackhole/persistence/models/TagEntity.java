package io.github.clamentos.blackhole.persistence.models;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Immutable data.</b></p>
 * <p>Tag resource entity.</p>
 * <p>This class represents the "tag" entity in the database.</p>
 * The getter methods are all thread safe and standard.
*/
public final record TagEntity(

    int id,
    String name,
    int creation_date

    //____________________________________________________________________________________________________________________________________

) {}