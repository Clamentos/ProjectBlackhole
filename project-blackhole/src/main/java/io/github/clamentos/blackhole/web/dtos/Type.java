package io.github.clamentos.blackhole.web.dtos;

public enum Type implements Streamable {
    
    BYTE(0),
    SHORT(1),
    INT(2),
    LONG(3),
    FLOAT(4),
    DOUBLE(5),
    STRING(6),
    RAW(7);

    private int val;

    private Type(int val) {

        this.val = val;
    }

    @Override
    public byte[] toBytes() {
        
        return(new byte[]{(byte)val});
    }
}
