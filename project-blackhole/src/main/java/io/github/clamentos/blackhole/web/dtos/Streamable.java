package io.github.clamentos.blackhole.web.dtos;

@FunctionalInterface
public interface Streamable {
    
    byte[] toBytes();
}
