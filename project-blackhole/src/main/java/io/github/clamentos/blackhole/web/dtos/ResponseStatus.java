package io.github.clamentos.blackhole.web.dtos;

public enum ResponseStatus {
    
    OK,
    ERROR;

    public byte streamify() {

        switch(this) {

            case OK: return(0);
            case ERROR: return(1);

            default: return(-1);
        }
    }
}
