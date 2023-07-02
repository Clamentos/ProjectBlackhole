package io.github.clamentos.blackhole.web.dtos;

import io.github.clamentos.blackhole.common.framework.Reducible;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Type;

import java.util.ArrayList;
import java.util.List;

public record ErrorDetails(

    long timestamp,
    String message

) implements Reducible {

    public ErrorDetails(String message) {

        this(System.currentTimeMillis(), message);
    }

    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> result = new ArrayList<>();

        result.add(new DataEntry(Type.LONG, timestamp));
        result.add(new DataEntry(Type.STRING, message));

        return(result);
    }
}
