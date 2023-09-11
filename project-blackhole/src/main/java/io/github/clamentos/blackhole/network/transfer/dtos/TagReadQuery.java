package io.github.clamentos.blackhole.network.transfer.dtos;

///
import io.github.clamentos.blackhole.network.transfer.Request;
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import io.github.clamentos.blackhole.utility.ArrayUtils;
import io.github.clamentos.blackhole.persistence.models.TagEntity;

import java.util.List;

///
/**
 * <h3>Tag read query DTO</h3>
 * 
 * This record class specifies the parameters used when requesting a read on the tag entity. The parameters
 * have the following meaning:
 * 
 * <ul>
 *     <li>{@code mode}: Specifies the query mode:
 *         <ul>
 *             <li>0: {@code SELECT ... IN (by_ids)}. The array must not be null, nor empty.
 *                 {@code by_names}, {@code by_name_like}, {@code creation_date_start}
 *                 and {@code creation_date_end} will be ignored.</li>
 *             <li>1: {@code SELECT ... IN (by_names)}. The array must not be null, nor empty.
 *                 {@code by_ids} must be null or empty, {@code by_name_like}, {@code creation_date_start}
 *                 and {@code creation_date_end} will be ignored.</li>
 *             <li>2: {@code SELECT ... WHERE name LIKE %code by_name_like% AND creation_date BETWEEN
 *                 creation_date_start AND creation_date_end}. {@code by_ids} and {@code by_names} will be
 *                 ignored.</li>
 *             <li>3 Selects all tags.</li>
 *             <li>4: COUNT all tags in the database.</li>
 *         </ul></li>
 *     <li>{@code fields}: Specifies which fields should be fetched, only used for modes 0, 1, 2 and 3.
 *         In all other cases it will be ignored. Each bit selects a field:
 *         <ul>
 *             <li>001: {@link TagEntity#id}</li>
 *             <li>010: {@link TagEntity#name}</li>
 *             <li>100: {@link TagEntity#creation_date}</li>
 *         </ul></li>
 *     <li>{@code start}: Specifies a starting id. Only used for modes 2 and 3. In all other cases, it will
 *         be ignored.</li>
 *     <li>{@code limit}: Specifies the maximum amount of entities that can be returned. Only used for modes
 *         2 and 3. In all other cases, it will be ignored. If this parameter is null while in mode 2 or 3,
 *         it will signify to fetch all remaining results.</li>
 *     <li>{@code by_name_like}: String used in the LIKE clause.</li>
 *     <li>{@code creation_date_start}: Starting date range.</li>
 *     <li>{@code creation_date_end}: Ending date range.</li>
 *     <li>{@code by_ids}: Unique tag id list.</li>
 *     <li>{@code by_names}: Unique tag name list.</li>
 * </ul>
 * 
 * @apiNote This class is <b>immutable data</b>.
*/
public record TagReadQuery(

    byte mode,
    Byte fields,
    Integer start,
    Integer limit,
    String by_name_like,
    Integer creation_date_start,
    Integer creation_date_end,
    List<Integer> by_ids,
    List<String> by_names

) {

    ///
    public String getByIdsValues() {

        String result = by_ids.toString();
        return(result.substring(1, result.length() - 1));
    }

    public String getByNamesValues() {

        String result = by_names.toString();
        return(result.substring(1, result.length() - 1));
    }

    ///
    /**
     * Instantiates a new {@link TagReadQuery} object.
     * @param request : The input request.
     * @return : The new {@link TagReadQuery}.
     * @throws IllegalArgumentException If {@code this.mode} was {@code null} or the data entries of the
     *                                  request did not conform to the ranges.
     * @throws IllegalStateException If {@code this.by_ids} or {@code this.by_names} were ill formed arrays.
     * @see {@link DataEntry}
    */
    public static TagReadQuery newInstance(Request request) throws IllegalArgumentException, IllegalStateException {

        byte mode;

        Byte fields = null;
        Integer start = null;
        Integer limit = null;
        String by_name_like = null;
        Integer creation_date_start = null;
        Integer creation_date_end = null;
        List<Integer> by_ids = null;
        List<String> by_names = null;

        int temp;

        List<DataEntry> data_entries = request.data();
        mode = data_entries.get(0).entryAsByte(false);

        switch(mode) {

            case 0: // Fetch by ids.

                fields = data_entries.get(1).entryAsByte(false);

                if(fields <= 0 && fields >= 4) {

                    throw new IllegalArgumentException("Field \"fields\" must be 1, 2 or 3. Got: " + fields);
                }

                by_ids = ArrayUtils.makeIntegerArray(data_entries, 7, data_entries.size() - 2, false);

            break;

            case 1: // Fetch by names.

                fields = data_entries.get(1).entryAsByte(false);

                if(fields <= 0 && fields >= 4) {

                    throw new IllegalArgumentException("Field \"fields\" must be 1, 2 or 3. Got: " + fields);
                }

                // The previous array ("by_ids") MUST be either NULL or empty (BEGIN,END)
                if(ArrayUtils.checkIfNullOrEmpty(data_entries, 7) == true) temp = 8;
                else temp = 9;

                by_names = ArrayUtils.makeStringArray(
                    
                    data_entries,
                    temp,
                    data_entries.size(), 
                    false,
                    "^[a-zA-Z0-9_-]{3,31}$"
                );

            break;

            case 2: // Fetch by mixed (paged).

                fields = data_entries.get(1).entryAsByte(false);

                if(fields <= 0 && fields >= 4) {

                    throw new IllegalArgumentException("Field \"fields\" must be 1, 2 or 3. Got: " + fields);
                }

                start = data_entries.get(2).entryAsInteger(false);
                limit = data_entries.get(3).entryAsInteger(true);
                by_name_like = data_entries.get(4).entryAsString("^[a-zA-Z0-9_-]{3,31}$", true);
                creation_date_start = data_entries.get(5).entryAsInteger(true);
                creation_date_end = data_entries.get(6).entryAsInteger(true);

                // TODO: date start and end must either both be defined or both null

            break;

            case 3: // Fetch all (paged).
            
                fields = data_entries.get(1).entryAsByte(false);

                if(fields <= 0 && fields >= 4) {

                    throw new IllegalArgumentException("Field \"fields\" must be 1, 2 or 3. Got: " + fields);
                }

                start = data_entries.get(2).entryAsInteger(false);
                limit = data_entries.get(3).entryAsInteger(true);

            break;

            case 4: break; // No other parameter is needed for this mode.

            default: throw new IllegalArgumentException("Illegal mode. It must be: 0, 1, 2, 3 or 4. Got: " + mode);
        }

        return(new TagReadQuery(
            
            mode,
            fields,
            start,
            limit,
            by_name_like,
            creation_date_start,
            creation_date_end,
            by_ids,
            by_names
        ));
    }

    ///
}
