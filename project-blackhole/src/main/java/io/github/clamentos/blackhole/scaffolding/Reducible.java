package io.github.clamentos.blackhole.scaffolding;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.network.transfer.components.DataEntry;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Reducible interface.</h3>
 * Used to indicate that the implementing classes can be transformed into semi-structured data.
 * @see {@link DataEntry}
 * @apiNote This class is a <b>Functional interface</b>.
*/
@FunctionalInterface
public interface Reducible {

    //____________________________________________________________________________________________________________________________________
    
    /** @return A never {@code null} list of {@link DataEntry} representing {@code this}. */
    List<DataEntry> reduce();

    //____________________________________________________________________________________________________________________________________
}
