package io.github.clamentos.blackhole.network.request.components;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Enumeration class.</b></p>
 * Simple enumeration containing all the possible actions that can be done on {@link Resources}.
 * <ul>
 *     <li>{@code CREATE}: Specifies a create type action.</li>
 *     <li>{@code READ}: Specifies a read type action.</li>
 *     <li>{@code UPDATE}: Specifies an update type action.</li>
 *     <li>{@code DELETE}: Specifies a delete type action.</li>
 *     <li>{@code LOGIN}: Specifies a login type action. Only possible on {@link Resources#USER}.</li>
 *     <li>{@code LOGOUT}: Specifies a logout type action. Only possible on {@link Resources#USER}.</li>
 * </ul>
*/
public enum Methods {
    
    CREATE,
    READ,
    UPDATE,
    DELETE,
    LOGIN,
    LOGOUT;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * @param method_id : The method identifier.
     * @return The corresponding {@link Methods} constant.
     * @throws IllegalArgumentException If {@code method_id} is not
     *         {@code 0}, {@code 1}, {@code 2}, {@code 3}, {@code 4} or {@code 5}.
    */
    public static Methods newInstance(byte method_id) throws IllegalArgumentException {

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

    //____________________________________________________________________________________________________________________________________
}
