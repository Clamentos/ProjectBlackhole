package io.github.clamentos.blackhole.persistence.models;

public record ReportTypeEntity(

    short id,
    int creation_date,
    int last_modified,
    String name

) {}
