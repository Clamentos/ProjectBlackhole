package io.github.clamentos.blackhole.scaffolding;

///
import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import java.util.List;

///
/**
 * <h3>Projectable interface</h3>
 * Used to indicate that the implementing classes can be transformed into semi-structured data,
 * with the option of choosing which fields to consider.
*/
public interface Projectable extends Reducible {
    
    /**
     * @param fields : The fields to consider. This parameter works as a "checklist"
     *                 starting from the first class field which maps to least significant bit.
     * @return A never {@code null} list of {@link DataEntry} representing {@code this}.
    */
    List<DataEntry> project(long fields);

    ///
}
