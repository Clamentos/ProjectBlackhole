package io.github.clamentos.blackhole.business.models;

public record UpdateNoteEntity(

    long id,
    int creation_date,
    String note,

    UserEntity user

) {}
