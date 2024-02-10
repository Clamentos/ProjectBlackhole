package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
/**
 * <h3>Data Transfer Object</h3>
 * <p>Specifies that the implementing class is a data-transfer-object (DTO).</p>
 * <b>NOTE: this interface is intended for deserialization only.</b> Serialization is done via the {@code Streamable} contract.
*/
@FunctionalInterface
public interface DataTransferObject {

    ///
    /** @return {@code true} if {@code this} data-transfer-object is "reactive", {@code false} otherwise. */
    boolean isReactive();

    ///
}
