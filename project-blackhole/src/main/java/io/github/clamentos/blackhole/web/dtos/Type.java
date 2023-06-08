package io.github.clamentos.blackhole.web.dtos;

public enum Type {
    
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    STRING,
    RAW;

    public byte streamify() {

        switch(this) {

            case BYTE: return(0);
            case SHORT: return(1);
            case INT: return(2);
            case LONG: return(3);
            case FLOAT: return(4);
            case DOUBLE: return(5);
            case STRING: return(6);
            case RAW: return(7);

            default: return(-1);
        }
    }
}
