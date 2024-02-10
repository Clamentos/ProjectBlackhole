package io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization;

///
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Resources;

///
/**
 * <h3>Deserializer Provider</h3>
 * Specifies that the implementing class can provide the user-defined deserializers to other classes.
 * @see Deserializer
*/
@FunctionalInterface
public interface DeserializerProvider {

    ///
    /**
     * Gets the deserializer associated to the provided resource.
     * @param resource : The target resource associated to a unique deserializer.
     * @return The never {@code null} deserializer.
     * @see Resources
     * @see Deserializer
    */
    Deserializer getDeserializer(Resources<? extends Enum<?>> resource);

    ///
}
