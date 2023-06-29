package io.github.clamentos.blackhole.web.dtos.components;

import io.github.clamentos.blackhole.common.framework.Streamable;

public enum ResponseStatus implements Streamable {

    OK,
    DENIED,
    UNAUTHENTICATED,
    NOT_FOUND,
    METHOD_NOT_ALLOWED,
    ERROR;

    @Override
    public byte[] toBytes() {

        switch(this) {

            case OK: return(new byte[]{0});
            case DENIED: return(new byte[]{1});
            case UNAUTHENTICATED: return(new byte[]{2});
            case NOT_FOUND: return(new byte[]{3});
            case METHOD_NOT_ALLOWED: return(new byte[]{4});
            case ERROR: return(new byte[]{5});

            default: return(new byte[]{6});
        }
    }
}
