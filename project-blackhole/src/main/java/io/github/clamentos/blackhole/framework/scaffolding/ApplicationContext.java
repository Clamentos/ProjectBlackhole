package io.github.clamentos.blackhole.framework.scaffolding;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.controller.ServletProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.DeserializerProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.ResourcesProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.validation.ValidatorProvider;

///
/**
 * <h3>Application Context</h3>
 * Specifies that the implementing class can provide services to other classes.
*/
public interface ApplicationContext {

    ///
    /** @return The never {@code null} user defined resources provider. */
    ResourcesProvider<? extends Enum<?>> getResourcesProvider();

    ///..
    /** @return The never {@code null} user defined servlet provider. */
    ServletProvider getServletProvider();

    ///..
    /** @return The never {@code null} user defined deserializer provider. */
    DeserializerProvider getDeserializerProvider();

    ///..
    /** @return The never {@code null} user defined validator provider. */
    ValidatorProvider getValidatorProvider();

    ///
}
