package io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization;

///
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DeserializationException;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Methods;

///
/**
 * <h3>Deserializer</h3>
 * Specifies that the implementing class can deserialize data coming from a data provider into a data-transfer-object.
 * @see DataProvider
 * @see Methods
 * @see Deserializable
*/
@FunctionalInterface
public interface Deserializer {

    ///
    /**
     * Deserializes data coming from the data provider into a data-transfer-object.
     * @param in : The data provider as the source of incoming data.
     * @param payload_size : The size of the payload specified in the request headers.
     * @param request_method : The method of the incoming request.
     * @return The never {@code null} deserialized data-transfer-object.
     * @throws DeserializationException If any error occurs during the deserialization process.
     * @see DataProvider
     * @see Methods
     * @see Deserializable
    */
    Deserializable deserialize(DataProvider in, long payload_size, Methods request_method) throws DeserializationException;

    ///
}
