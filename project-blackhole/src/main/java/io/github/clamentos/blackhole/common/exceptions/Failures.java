package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

// Only used for JavaDocs.
import io.github.clamentos.blackhole.network.request.components.ResponseStatuses;

//________________________________________________________________________________________________________________________________________

/**
 * <ul>
 *     <li><b>BEHAVIOUR: Enumeration.</b></li>
 *     <li><b>STEREOTYPE: Error signaling.</b></li>
 * </ul>
 * Enumeration of all possible failures. These codes will be translated into actual
 * {@link ResponseStatuses}.
 * <ul>
 *     <li>{@code SESSION_NOT_FOUND}: The request specified a non existant session id.</li>
 *     <li>{@code SESSION_EXPIRED}: The request specified an expired session id.</li>
 *     <li>{@code NOT_ENOUGH_PRIVILEGES}: The requesting user doesn't have enough privileges
 *         to perform the desired action.</li>
 *     <li>{@code BAD_FORMATTING}: The request was syntactically malformed.</li>
 *     <li>{@code IO_ERROR}: Generic I/O error.</li>
 *     <li>{@code TOO_MANY_CONNECTIONS}: The same ip opened too many sockets.</li>
 *     <li>{@code TOO_MANY_REQUESTS}: The socket exhausted the request count.</li>
 *     <li>{@code CLIENT_TOO_SLOW}: The client transmitting speed was too low.</li>
 *     <li>{@code CLIENT_TIMED_OUT}: The client was inactive for too long.</li>
 *     <li>{@code END_OF_STREAM}: End of stream reached.</li>
 *     <li>{@code UNSUPPORTED_METHOD}: The request specified an unsupported method
 *         for the target resource.</li>
 * </ul>
*/
public enum Failures {
    
    SESSION_NOT_FOUND,
    SESSION_EXPIRED,
    NOT_ENOUGH_PRIVILEGES,
    BAD_FORMATTING,
    IO_ERROR,
    TOO_MANY_CONNECTIONS,
    TOO_MANY_REQUESTS,
    CLIENT_TOO_SLOW,
    CLIENT_TIMED_OUT,
    END_OF_STREAM,
    UNSUPPORTED_METHOD;

    //____________________________________________________________________________________________________________________________________
}
