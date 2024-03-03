package io.github.clamentos.blackhole.framework.scaffolding.network.deserialization;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Resources;

///
/**
 * <h3>Deserializer Provider</h3>
 * Specifies that the implementing class can provide the user defined deserializers to other classes.
*/
@FunctionalInterface
public interface DeserializerProvider {

    ///
    /**
     * Gets the unique deserializer associated to the provided resource.
     * @param resource : The target resource.
     * @return The never {@code null} deserializer.
    */
    Deserializer getDeserializer(Resources<? extends Enum<?>> resource);

    ///
}
