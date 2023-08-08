package io.github.clamentos.blackhole.network.transfer.dtos;

import java.util.ArrayList;
import java.util.List;

import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.network.transfer.components.Types;
import io.github.clamentos.blackhole.persistence.models.TagEntity;
import io.github.clamentos.blackhole.scaffolding.Reducible;

public record TagDto(

    List<TagEntity> tags

) implements Reducible {

    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> entries = new ArrayList<>();

        entries.add(new DataEntry(Types.BEGIN, null));

        for(TagEntity tag : tags) {

            entries.addAll(tag.reduce());
        }

        entries.add(new DataEntry(Types.END, null));
        return(entries);
    }
}
