package io.github.clamentos.blackhole.framework;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.network.request.components.DataEntry;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Reducible interface.</p>
 * Used to indicate that the implementing classes can be transformed into a list of {@link DataEntry}.
*/
@FunctionalInterface
public interface Reducible {

    //____________________________________________________________________________________________________________________________________
    
    /** @return A never {@code null} list of {@link DataEntry} representing {@code this}. */
    List<DataEntry> reduce();

    //____________________________________________________________________________________________________________________________________
}
