package io.github.clamentos.blackhole.web.dtos;

import io.github.clamentos.blackhole.common.framework.Streamable;

public record UserDetails(

    byte[] session_id

) implements Streamable {

    @Override
    public byte[] toBytes() {

        return(session_id);
    }
}
