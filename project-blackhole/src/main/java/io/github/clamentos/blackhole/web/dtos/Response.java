package io.github.clamentos.blackhole.web.dtos;

public record Response(

    byte response_status,
    DataEntry data_entry
) {}
