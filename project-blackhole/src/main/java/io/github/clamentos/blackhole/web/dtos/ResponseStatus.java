package io.github.clamentos.blackhole.web.dtos;

public enum ResponseStatus implements Streamable {

    OK,
    DENIED,
    UNAUTHENTICATED,
    NOT_FOUND,
    ERROR;

    @Override
    public byte[] toBytes() {

        switch(this) {

            case OK: return(new byte[]{0});
            case DENIED: return(new byte[]{1});
            case UNAUTHENTICATED: return(new byte[]{2});
            case NOT_FOUND: return(new byte[]{3});
            case ERROR: return(new byte[]{4});

            default: return(new byte[]{5});
        }
    }
}
