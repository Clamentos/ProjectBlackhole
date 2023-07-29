package io.github.clamentos.blackhole.network.request.components;

//________________________________________________________________________________________________________________________________________

// TODO: put links in the doc
/**
 * Simple enumeration containing all the possible resource types in the system.
 * <ul>
 *     <li>{@code SYSTEM}: Targets the System entity, which can be used to aquire various stats
 *         about the currently running system.</li>
 *     <li>{@code USER}: Targets the User entity.</li>
 *     <li>{@code TAG}: Targets the Tag entity.</li>
 *     <li>{@code RESOURCE}: Targets the Resource entity.</li>
 *     <li>{@code TYPE}: Targets the Type entity.</li>
 * </ul>
*/
public enum Resources {
    
    SYSTEM,
    USER,
    TAG,
    RESOURCE,
    TYPE;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * @param resource_id : The type of resource.
     * @return The corresponding {@link Resources} constant.
     * @throws IllegalArgumentException If {@code resource_id} is not
     *         {@code 0}, {@code 1}, {@code 2}, {@code 3} or {@code 4}.
    */
    public static Resources newInstance(byte resource_id) throws IllegalArgumentException {

        switch(resource_id) {

            case 0: return(Resources.SYSTEM);
            case 1: return(Resources.USER);
            case 2: return(Resources.TAG);
            case 3: return(Resources.RESOURCE);
            case 4: return(Resources.TYPE);

            default: throw new IllegalArgumentException("Unknown resource type");
        }
    }

    //____________________________________________________________________________________________________________________________________

}
