package io.github.clamentos.blackhole.persistence.entities;

/**
 * <p><b>Entity</b></p>
 * User.
*/
public record User(

    int id,
    String username,
    String email,
    String password_hash,
    int registration_time,
    int last_updated
    //...

) {}
