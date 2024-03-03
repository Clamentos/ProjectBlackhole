package io.github.clamentos.blackhole.framework.scaffolding.network.controller;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Resources;

///
/**
 * <h3>Servlet Provider</h3>
 * Specifies that the implementing class can provide servlets to other classes.
*/
@FunctionalInterface
public interface ServletProvider {

    ///
    /**
     * Gets the unique servlet associated to the provided resource.
     * @param resource : The target resource.
     * @return The never {@code null} servlet.
    */
    Servlet getServlet(Resources<? extends Enum<?>> resource);

    ///
}
