package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
/**
 * <h3>Data transfer object</h3>
 * Specifies that the implementing class is a data-transfer-object (DTO).
*/
@FunctionalInterface
public interface DataTransferObject {

    ///
    /** @return {@code true} if {@code this} data-transfer-object is "reactive", {@code false} otherwise. */
    boolean isReactive();

    ///
}
