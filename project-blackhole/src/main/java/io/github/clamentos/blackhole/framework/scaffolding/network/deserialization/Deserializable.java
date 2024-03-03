package io.github.clamentos.blackhole.framework.scaffolding.network.deserialization;

///
/**
 * <h3>Deserializable</h3>
 * Specifies that the implementing class can be deserialized from a stream into a structured object.
*/
@FunctionalInterface
public interface Deserializable {

    ///
    /**
     * @return {@code true} if {@code this} is streaming, {@code false} otherwise.
     * @apiNote "streaming" signifies that {@code this} object can carry very large amounts of data that are not supposed to be kept
     * in memory and an input stream is specified instead.
    */
    boolean isStreaming();

    ///
}
