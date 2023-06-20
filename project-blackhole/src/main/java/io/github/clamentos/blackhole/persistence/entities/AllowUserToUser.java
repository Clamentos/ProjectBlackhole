package io.github.clamentos.blackhole.persistence.entities;

public record AllowUserToUser(

    Integer user_id,
    Integer target_user_id,
    Byte flags
    
) {}
