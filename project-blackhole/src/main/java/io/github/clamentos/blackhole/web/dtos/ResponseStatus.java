package io.github.clamentos.blackhole.web.dtos;

import java.util.List;

public enum ResponseStatus implements Streamable {

    OK,
    DENIED,
    UNAUTHENTICATED,
    NOT_FOUND,
    ERROR;

    @Override
    public List<Byte> toBytes() {

        switch(this) {

            case OK: return(List.of((byte)0));
            case DENIED: return(List.of((byte)1));
            case UNAUTHENTICATED: return(List.of((byte)2));
            case NOT_FOUND: return(List.of((byte)3));
            case ERROR: return(List.of((byte)4));

            default: return(List.of((byte)-1));
        }
    }
}
