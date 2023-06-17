package io.github.clamentos.blackhole.web.dtos;

@FunctionalInterface
public interface Streamable {
    
    /**
     * Transform {@code this} into raw bytes of data.
     * @return An array of bytes representing {@code this}
     */
    byte[] toBytes();
}
