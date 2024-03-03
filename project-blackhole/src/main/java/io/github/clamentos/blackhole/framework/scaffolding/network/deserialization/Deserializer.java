package io.github.clamentos.blackhole.framework.scaffolding.network.deserialization;

///
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DeserializationException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Methods;

///
/**
 * <h3>Deserializer</h3>
 * Specifies that the implementing class can deserialize data coming from a data provider into a POJO.
*/
@FunctionalInterface
public interface Deserializer {

    ///
    /**
     * Deserializes data coming from the data provider into a POJO.
     * @param in : The data provider as the source of incoming data.
     * @param payload_size : The size of the payload specified in the request headers.
     * @param request_method : The method of the incoming request.
     * @return The never {@code null} deserialized POJO.
     * @throws DeserializationException If any error occurs during the deserialization process.
    */
    Deserializable deserialize(DataProvider in, long payload_size, Methods request_method) throws DeserializationException;

    ///
}
