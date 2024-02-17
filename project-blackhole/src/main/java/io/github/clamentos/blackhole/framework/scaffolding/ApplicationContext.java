package io.github.clamentos.blackhole.framework.scaffolding;

///
import io.github.clamentos.blackhole.framework.scaffolding.servlet.ServletProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.DeserializerProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResourcesProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.validation.ValidatorProvider;

///
/**
 * <h3>Application Context</h3>
 * Specifies that the implementing class can provide services to other classes.
 * @see ServletProvider
 * @see ResourcesProvider
 * @see DeserializerProvider
*/
public interface ApplicationContext {

    ///
    /**
     * @return The never {@code null} user defined servlet provider.
     * @see ServletProvider
    */
    ServletProvider getServletProvider();

    ///..
    /**
     * @return The never {@code null} user defined resources provider.
     * @see ResourcesProvider
    */
    ResourcesProvider<? extends Enum<?>> getResourcesProvider();

    ///..
    /**
     * @return The never {@code null} user defined deserializer provider.
     * @see DeserializerProvider
    */
    DeserializerProvider getDeserializerProvider();

    ///..
    /**
     * @return The never {@code null} user defined validator provider.
     * @see ValidatorProvider
    */
    ValidatorProvider getValidatorProvider();

    ///
}
