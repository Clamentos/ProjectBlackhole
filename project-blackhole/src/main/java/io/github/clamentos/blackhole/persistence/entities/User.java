package io.github.clamentos.blackhole.persistence.entities;

public record User(

    int id,
    String username,
    String email,
    String password_hash,
    int registration_time,
    int last_updated
    //...

) {}
