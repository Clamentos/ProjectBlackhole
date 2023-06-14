package io.github.clamentos.blackhole.persistence.entities;

/**
 * <p><b>Entity</b></p>
 * User.
*/
public record User(

    Integer id,
    String username,
    String email,
    String password_hash,
    Integer registration_time,
    Integer last_updated
    //...

) {}
