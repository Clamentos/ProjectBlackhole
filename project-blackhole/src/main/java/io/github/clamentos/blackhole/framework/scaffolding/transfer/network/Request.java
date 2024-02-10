package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
/**
 * <h3>Request</h3>
 * Specifies that the implementing class is a network request.
*/
public interface Request {

    ///
    byte getId();
    
    ///..
    /**
     * @return The never {@code null} resource that {@code this} request is targeting.
     * @see Resources
    */
    Resources<? extends Enum<?>> getResource();

    ///..
    /**
     * @return The possibly {@code null} data transfer object that {@code this} request is carrying.
     * @see DataTransferObject
    */
    DataTransferObject getDataTransferObject();

    ///
}
