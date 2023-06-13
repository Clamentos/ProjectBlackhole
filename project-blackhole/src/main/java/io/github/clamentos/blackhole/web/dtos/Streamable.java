package io.github.clamentos.blackhole.web.dtos;

import java.util.List;

@FunctionalInterface
public interface Streamable {
    
    List<Byte> toBytes();
}
