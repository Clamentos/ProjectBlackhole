package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
/**
 * <h3>Methods</h3>
 * Enumeration containing all possible actions that a network request can specify.
 * <ol>
 *     <li>{@code CREATE}: This method indicates a creation type action of a new resource.</li>
 *     <li>{@code READ}: This method indicates a read type action of an existing resource.</li>
 *     <li>{@code UPDATE}: This method indicates an update type action of an existing resource.</li>
 *     <li>{@code DELETE}: This method indicates a delete type action of an existing resource.</li>
 * </ol>
 * @see Request
*/
public enum Methods {

    ///
    CREATE,
    READ,
    UPDATE,
    DELETE;

    ///
    /**
     * Instantiates a new {@link Methods} object.
     * @param method_id : The method identifier, that is, the corresponding ordinal of the desired constant.
     * @return The never {@code null} corresponding constant.
     * @throws IllegalArgumentException If {@code method_id} is not: {@code 0}, {@code 1}, {@code 2} or {@code 3}.
    */
    public static Methods newInstance(byte method_id) throws IllegalArgumentException {

        switch(method_id) {

            case 0: return(Methods.CREATE);
            case 1: return(Methods.READ);
            case 2: return(Methods.UPDATE);
            case 3: return(Methods.DELETE);

            default: throw new IllegalArgumentException("(Methods.newInstance) -> Unknown request method: " + method_id);
        }
    }

    ///
}
