package io.github.clamentos.blackhole.web.request.components;

public enum Methods {
    
    CREATE,
    READ,
    UPDATE,
    DELETE,
    LOGIN,
    LOGOUT;

    public static Methods construct(byte method_id) throws IllegalArgumentException {

        switch(method_id) {

            case 0: return(Methods.CREATE);
            case 1: return(Methods.READ);
            case 2: return(Methods.UPDATE);
            case 3: return(Methods.DELETE);
            case 4: return(Methods.LOGIN);
            case 5: return(Methods.LOGOUT);

            default: throw new IllegalArgumentException("Unknown request method");
        }
    }
}
