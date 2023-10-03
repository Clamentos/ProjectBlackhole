package io.github.clamentos.blackhole.persistence.models;

public record UpdateNoteEntity(

    long id,
    int creation_date,
    String note,

    UserEntity user

) {}
