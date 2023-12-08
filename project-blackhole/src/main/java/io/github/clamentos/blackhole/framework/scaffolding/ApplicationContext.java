package io.github.clamentos.blackhole.framework.scaffolding;

///
import io.github.clamentos.blackhole.framework.scaffolding.servlet.ServletProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializer;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResourcesProvider;

///
/**
 * <h3>Application context</h3>
 * Specifies that the implementing class can provide services to other classes.
 * @see ServletProvider
 * @see ResourcesProvider
 * @see Deserializer
*/
public interface ApplicationContext {

    ///
    /**
     * @return The never {@code null} user-defined servlet provider.
     * @see ServletProvider
    */
    ServletProvider getServletProvider();

    ///..
    /**
     * @return The never {@code null} user-defined resources provider.
     * @see ResourcesProvider
    */
    ResourcesProvider<? extends Enum<?>> getResourcesProvider();

    ///..
    /**
     * @return The never {@code null} user-defined deserializer.
     * @see Deserializer
    */
    Deserializer getDeserializer();

    ///
}
