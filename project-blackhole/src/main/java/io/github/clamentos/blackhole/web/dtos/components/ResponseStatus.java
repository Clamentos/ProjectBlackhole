package io.github.clamentos.blackhole.web.dtos.components;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Streamable;

//________________________________________________________________________________________________________________________________________

/**
 * Simple enumeration containing all the possible response statuses.
 * <ul>
 *     <li>OK: request was successfull.</li>
 *     <li>DENIED: requester didn't have enough privileges.</li>
 *     <li>UNAUTHENTICATED: requester wasn't authenticated.</li>
 *     <li>NOT_FOUND: request didn't find anything.</li>
 *     <li>METHOD_NOT_ALLOWED: request specified an unknown or illegal method (see {@link Method}).</li>
 *     <li>UNKNOWN_RESOURCE_TYPE: request specified an unknown resource type (see {@link Entities}).</li>
 *     <li>BAD_REQUEST: request wasn't syntactically well formed.</li>
 *     <li>UNPROCESSABLE_REQUEST: request wasn't semantically well formed.</li>
 *     <li>ERROR: internal server error.</li>
 * </ul>
*/
public enum ResponseStatus implements Streamable {

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
     * {@inheritDoc}
    */
    @Override
    public byte[] stream() {

        switch(this) {

            case OK: return(new byte[]{0});
            case DENIED: return(new byte[]{1});
            case UNAUTHENTICATED: return(new byte[]{2});
            case NOT_FOUND: return(new byte[]{3});
            case METHOD_NOT_ALLOWED: return(new byte[]{4});
            case UNKNOWN_RESOURCE_TYPE: return(new byte[]{5});
            case BAD_REQUEST: return(new byte[]{6});
            case UNPROCESSABLE_REQUEST: return(new byte[]{7});
            case ERROR: return(new byte[]{8});

            default: return(new byte[]{-1});
        }
    }

    //____________________________________________________________________________________________________________________________________
}
