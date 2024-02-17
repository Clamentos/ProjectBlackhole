package io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization;

///
/**
 * <h3>Deserializable</h3>
 * Specifies that the implementing class can be deserialized from a stream into a structured object.
*/
@FunctionalInterface
public interface Deserializable {

    ///
    /** @return {@code true} if {@code this} is "reactive", {@code false} otherwise. */
    boolean isReactive();

    ///
}
