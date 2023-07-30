package io.github.clamentos.blackhole.persistence.models;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Immutable data.</b></p>
 * <p>Type resource entity.</p>
 * <p>This class represents the "type" entity in the database.</p>
 * The getter methods are all thread safe and standard.
*/
public record TypeEntity(

    short id,
    String name,
    boolean is_complex

    //____________________________________________________________________________________________________________________________________

) {}
