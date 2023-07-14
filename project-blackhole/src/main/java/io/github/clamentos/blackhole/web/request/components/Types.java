package io.github.clamentos.blackhole.web.request.components;

public enum Types {
    
    BYTE(0),
    SHORT(1),
    INT(2),
    LONG(3),
    FLOAT(4),
    DOUBLE(5),
    STRING(6),
    RAW(7),
    NULL(8);

    //____________________________________________________________________________________________________________________________________

    private int val;

    //____________________________________________________________________________________________________________________________________

    private Types(int val) {

        this.val = val;
    }

    //____________________________________________________________________________________________________________________________________

    public int getVal() {

        return(val);
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public String toString() {

        switch(this) {

            case BYTE: return("BYTE");
            case SHORT: return("SHORT");
            case INT: return("INT");
            case LONG: return("LONG");
            case FLOAT: return("FLOAT");
            case DOUBLE: return("DOUBLE");
            case STRING: return("STRING");
            case RAW: return("RAW");
            case NULL: return("NULL");

            default: return("NDF");
        }
    }

    //____________________________________________________________________________________________________________________________________
}
