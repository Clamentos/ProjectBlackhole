package io.github.clamentos.blackhole.web.dtos;

import java.util.List;

public record Request(

    Method method,
    byte[] session_id,
    List<DataEntry> data_entries

) {}
