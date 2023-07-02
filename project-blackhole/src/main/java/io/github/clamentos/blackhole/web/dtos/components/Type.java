package io.github.clamentos.blackhole.web.dtos.components;

//________________________________________________________________________________________________________________________________________

/**
 * Simple enumeration containing all the possible types of types.
*/
public enum Type {
    
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

    private Type(int val) {

        this.val = val;
    }

    //____________________________________________________________________________________________________________________________________

    public int getVal() {

        return(val);
    }

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
