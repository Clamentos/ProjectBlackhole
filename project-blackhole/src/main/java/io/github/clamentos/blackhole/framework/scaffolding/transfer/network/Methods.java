package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
/**
 * <h3>Methods</h3>
 * Enumeration containing all possible actions that a network request can specify.
 * <ul>
 *     <li>{@code CREATE}: Specifies a create type action.</li>
 *     <li>{@code READ}: Specifies a read type action.</li>
 *     <li>{@code UPDATE}: Specifies an update type action.</li>
 *     <li>{@code DELETE}: Specifies a delete type action.</li>
 *     <li>{@code LOGIN}: Specifies a login type action.</li>
 *     <li>{@code LOGOUT}: Specifies a logout type action.</li>
 * </ul>
 * @see Request
*/
public enum Methods {

    ///
    // Constants.
    
    CREATE,
    READ,
    UPDATE,
    DELETE,
    LOGIN,
    LOGOUT;

    ///
    // Class methods.

    /**
     * Instantiates a new {@link Methods} object.
     * @param method_id : The method identifier, that is, the corresponding ordinal of the desired constant.
     * @return The never {@code null} corresponding constant.
     * @throws IllegalArgumentException If {@code method_id} is not: {@code 0}, {@code 1}, {@code 2}, {@code 3}, {@code 4} or {@code 5}.
    */
    public static Methods newInstance(byte method_id) throws IllegalArgumentException {

        switch(method_id) {

            case 0: return(Methods.CREATE);
            case 1: return(Methods.READ);
            case 2: return(Methods.UPDATE);
            case 3: return(Methods.DELETE);
            case 4: return(Methods.LOGIN);
            case 5: return(Methods.LOGOUT);

            default: throw new IllegalArgumentException("(Methods.newInstance) -> Unknown request method: " + method_id);
        }
    }

    ///
}
