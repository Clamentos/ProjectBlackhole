package io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization;

///
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DeserializationException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.DataTransferObject;

///
/**
 * <h3>Deserializer</h3>
 * Specifies that the implementing class can deserialize data coming from a data provider into a data-transfer-object.
 * @see DataProvider
 * @see DataTransferObject
*/
public interface Deserializer {

    ///
    /**
     * Deserializes data coming from the data provider into a data-transfer-object.
     * @param in : The data provider as the source of data.
     * @return The never {@code null} deserialized data-transfer-object.
     * @throws DeserializationException If any deserialization error occurs.
     * @see DataProvider
     * @see DataTransferObject
    */
    DataTransferObject deserialize(DataProvider in) throws DeserializationException;

    ///..
    /** @return {@code true} if {@code this} deserializer is "reactive", {@code false} otherwise. */
    boolean isReactive();

    ///
}
