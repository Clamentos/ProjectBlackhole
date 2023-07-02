package io.github.clamentos.blackhole.web.dtos.actions;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;

import java.util.List;

//________________________________________________________________________________________________________________________________________

public record TagRead(

    byte query_mode,
    byte fields,
    Integer limit,
    int[] ids,
    String name_like,
    Integer start_date,
    Integer end_date
) {

    //____________________________________________________________________________________________________________________________________

    public static TagRead deserialize(List<DataEntry> entries) throws IllegalArgumentException {

        byte query_mode;
        byte fields;
        int limit;
        int[] ids;
        String name_like;
        int start_date;
        int end_date;

        query_mode = Converter.entryToByte(entries.get(0));
        fields = Converter.entryToByte(entries.get(1));

        if(query_mode == 0) {

            ids = new int[Converter.entryToInt(entries.get(2))];

            for(int i = 0; i < ids.length; i++) {

                ids[i] = Converter.entryToInt(entries.get(i + 3));
            }

            return(new TagRead(query_mode, fields, 0, ids, null, 0, 0));
        }

        if(query_mode == 1) {
            
            limit = Converter.entryToIntNullable(entries.get(2));
            name_like = Converter.entryToString(entries.get(3));
            start_date = Converter.entryToIntNullable(entries.get(4));
            end_date = Converter.entryToIntNullable(entries.get(5));

            return(new TagRead(query_mode, fields, limit, null, name_like, start_date, end_date));
        }

        else {

            throw new IllegalArgumentException("Query mode must be 0 or 1, got: " + query_mode);
        }
    }

    //____________________________________________________________________________________________________________________________________
}

/*
 * possible queries:
 * 
 *     0) simple fetch:
 * 
 *         byte fields ->       which fields to get
 *         int ids_len ->       length of the array
 *         int[] ids ->         select with a list of ids
 * 
 *     1) complex fetch:
 * 
 *         byte fields      ->  which fields to get
 *         int limit        ->  paging
 *         String name_like ->  select with LIKE %name_like%
 *         int start_date   ->  select with given date interval (use end_date)
 *         int end_date
 * 
 * 
 * 
 * 
 * 
 * |query_mode(0)|fields(?)|ids(?)|
 * |query_mode(1)|fields(?)|limit(?)|params(?)|...|
*/