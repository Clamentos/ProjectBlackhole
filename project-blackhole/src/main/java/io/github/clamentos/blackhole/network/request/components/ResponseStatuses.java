package io.github.clamentos.blackhole.network.request.components;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.framework.Streamable;

//________________________________________________________________________________________________________________________________________

/**
 * Simple enumeration containing all the possible response statuses.
 * <ul>
 *     <li>{@code OK}: Request was successfull.</li>
 *     <li>{@code DENIED}: Requester didn't have enough privileges.</li>
 *     <li>{@code UNAUTHENTICATED}: Requester wasn't authenticated.</li>
 *     <li>{@code NOT_FOUND}: Request didn't find anything.</li>
 *     <li>{@code METHOD_NOT_ALLOWED}: Request specified an unknown or illegal method
 *         (see {@link Methods}).</li>
 *     <li>{@code UNKNOWN_RESOURCE_TYPE}: Request specified an unknown resource type
 *         (see {@link Resources}).</li>
 *     <li>{@code BAD_REQUEST}: Request wasn't syntactically well formed.</li>
 *     <li>{@code UNPROCESSABLE_REQUEST}: Request wasn't semantically well formed.</li>
 *     <li>{@code ERROR}: Internal server error.</li>
 * </ul>
*/
public enum ResponseStatuses implements Streamable {

    OK,
    DENIED,
    UNAUTHENTICATED,
    NOT_FOUND,
    METHOD_NOT_ALLOWED,
    UNKNOWN_RESOURCE_TYPE,
    BAD_REQUEST,
    UNPROCESSABLE_REQUEST,
    ERROR;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public byte[] stream() {

        return(new byte[]{(byte)this.ordinal()});
    }

    //____________________________________________________________________________________________________________________________________
}
