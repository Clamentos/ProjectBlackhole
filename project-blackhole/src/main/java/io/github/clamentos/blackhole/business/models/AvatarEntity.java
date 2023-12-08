package io.github.clamentos.blackhole.business.models;

public record AvatarEntity(

    short id,
    int creation_date,
    int last_modified,
    byte[] data

) {}
