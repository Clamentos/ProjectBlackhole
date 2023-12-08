package io.github.clamentos.blackhole.business.dtos;

/*import io.github.clamentos.blackhole.business.models.TagEntity;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.framework.implementation.utility.ArrayUtils;
import io.github.clamentos.blackhole.framework.implementation.utility.IndirectInteger;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entity;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Filter;

import java.util.ArrayList;
import java.util.List;

public record TagFilter(

    long timestamp,                         // Query timestamp (helps caching)
    byte mode,                              // Query mode (treated as a boolean): 0 select, 1 count
    long fields,                            // Fields to return (non-empty checklist)

    List<Integer> by_ids,
    List<String> by_names,

    String name_like,
    int creation_date_start,
    int creation_date_end

) implements Filter {

    @Override
    public boolean isFiltered(Entity entity) {

        boolean name_like_match = true;
        boolean date_start_match = true;
        boolean date_end_match = true;

        if(entity instanceof TagEntity == false) {

            return(false);
        }

        TagEntity tag = (TagEntity)entity;

        if(by_ids != null && by_ids.size() > 0) {

            return(false);
        }

        if(by_names != null && by_names.size() > 0) {

            return(false);
        }

        if(name_like != null && !name_like.equals("")) {

            String cleaned_like = name_like.substring(0, name_like.length() - 1);
            name_like_match = (tag.name().substring(0, cleaned_like.length()).equals(cleaned_like));
        }

        if(creation_date_start > 0) {
         
            date_start_match = (tag.creation_date() >= creation_date_start);
        }

        if(creation_date_end > 0) {
         
            date_end_match = (tag.creation_date() <= creation_date_end);
        }

        return(name_like_match && date_start_match && date_end_match);
    }

    public static TagFilter newInstance(List<DataEntry> request_data) throws IllegalArgumentException {

        IndirectInteger idx = new IndirectInteger(3);

        long timestamp = request_data.get(0).entryAsLong(false);
        byte mode = request_data.get(1).entryAsByte(false);

        if(mode != 0 && mode != 1) {

            throw new IllegalArgumentException("The \"mode\" parameter can only be 0 or 1, got: " + mode);
        }

        long fields = request_data.get(2).entryAsLong(false);

        if(fields == 0) {

            throw new IllegalArgumentException("The \"fields\" parameter must be different 0");
        }

        List<Integer> by_ids;
        List<String> by_names;

        String name_like;
        int creation_date_start;
        int creation_date_end;

        by_ids = ArrayUtils.parseIntegerArray(request_data, idx, false);

        if(by_ids.size() > 0) {

            return(new TagFilter(

                timestamp,
                mode,
                fields,
                by_ids,
                new ArrayList<>(),
                "",
                0,
                0
            ));
        }

        by_names = ArrayUtils.parseStringArray(request_data, idx, false,"^[a-zA-Z0-9_-]{3,31}$");

        if(by_names.size() > 0) {

            return(new TagFilter(

                timestamp,
                mode,
                fields,
                new ArrayList<>(),
                by_names,
                "",
                0,
                0
            ));
        }

        name_like = request_data.get(idx.value++).entryAsString("^[a-zA-Z0-9_-]{3,31}$", true);
        creation_date_start = request_data.get(idx.value++).entryAsInteger(false);
        creation_date_end = request_data.get(idx.value).entryAsInteger(false);

        return(new TagFilter(

            timestamp,
            mode,
            fields,
            new ArrayList<>(),
            new ArrayList<>(),
            name_like == null ? "" : name_like,
            creation_date_start,
            creation_date_end
        ));
    }
}*/
