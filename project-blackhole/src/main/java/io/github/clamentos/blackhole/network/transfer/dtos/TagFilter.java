package io.github.clamentos.blackhole.network.transfer.dtos;

import io.github.clamentos.blackhole.network.transfer.components.DataEntry;

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

) {

    public static TagFilter newInstance(List<DataEntry> request_data) throws IllegalArgumentException {

        int[] idx = new int[]{3};

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

        by_ids = parseIntegerArray(request_data, idx);

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

        by_names = parseStringsArray(request_data, idx);

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

        name_like = request_data.get(idx[0]++).entryAsString("^[a-zA-Z0-9_-]{3,31}$", true);
        creation_date_start = request_data.get(idx[0]++).entryAsInteger(false);
        creation_date_end = request_data.get(idx[0]).entryAsInteger(false);

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

    // Updates the index too!
    private static List<Integer> parseIntegerArray(List<DataEntry> request_data, int[] start) throws IllegalArgumentException {

        return(null);
    }

    // Updates the index too!
    private static List<String> parseStringsArray(List<DataEntry> request_data, int[] start) throws IllegalArgumentException {

        return(null);
    }
}
