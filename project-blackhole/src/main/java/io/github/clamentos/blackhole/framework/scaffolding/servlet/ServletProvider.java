package io.github.clamentos.blackhole.framework.scaffolding.servlet;

///
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Resources;

///
/**
 * <h3>Servlet Provider</h3>
 * Specifies that the implementing class can provide the user-defined servlets to other classes.
 * @see Servlet
*/
@FunctionalInterface
public interface ServletProvider {

    ///
    /**
     * Gets the servlet associated to the provided resource.
     * @param resource : The target resource associated to a unique servlet.
     * @return The never {@code null} servlet.
     * @see Resources
     * @see Servlet
    */
    Servlet getServlet(Resources<? extends Enum<?>> resource);

    ///
}
