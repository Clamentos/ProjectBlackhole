package io.github.clamentos.blackhole.web.dtos;

public record Request(

    byte[] session_id,
    DataEntry[] data_entries

) {}
