package io.github.clamentos.blackhole.persistence.models;

public record UserEntity(

    int id,
    String username,
    String email,
    String password_hash,
    int creation_date,
    int last_modified,
    String about,
    short role_id

) {}
