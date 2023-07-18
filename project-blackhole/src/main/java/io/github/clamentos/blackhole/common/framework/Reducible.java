// OK
package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.web.request.components.DataEntry;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Reducible interface.</p>
 * Used to indicate that the implementing classes
 * can be transformed into a list of {@link DataEntry}.
*/
@FunctionalInterface
public interface Reducible {

    //____________________________________________________________________________________________________________________________________
    
    /**
     * Transform {@code this} into a list of {@link DataEntry} in order to later be streamed into bytes.
     * @return A never null list {@link DataEntry} representing {@code this}.
    */
    List<DataEntry> reduce();

    //____________________________________________________________________________________________________________________________________
}
