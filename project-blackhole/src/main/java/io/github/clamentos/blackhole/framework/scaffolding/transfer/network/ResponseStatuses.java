package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
/**
 * <h3>Response Statuses</h3>
 * Enumeration containing all the possible response statuses.
 * <ol>
 *     <li>{@code OK}: The Request was processed successfully.</li>
 *     <li>{@code INTERNAL_ERROR}: Unexpected or unhandled server error.</li>
 *     <li>{@code NOT_MODIFIED}: The client holds the most up to date copy of the requested resource.</li>
 *     <li>{@code UNAUTHENTICATED}: The client is not authenticated.</li>
 *     <li>{@code UNAUTHORIZED}: The client is not authorized.</li>
 *     <li>{@code UNKNOWN_METHOD}: No matching request method found.</li>
 *     <li>{@code FORBIDDEN_METHOD}: The specified request method is not allowed for the target resource.</li>
 *     <li>{@code UNKNOWN_RESOURCE}: No matching request resource found.</li>
 *     <li>{@code BAD_FORMATTING}: The request is structurally malformed.</li>
 *     <li>{@code VALIDATION_ERROR}: The request contains illegal or invalid parameter values.</li>
 *     <li>{@code PAYLOAD_TOO_BIG}: The request payload is too big.</li>
 *     <li>{@code CONNECTION_TIMEOUT}: The connection was open for too long without receiving any data.</li>
 *     <li>{@code BROKEN_STREAM}: The connection ended prematurely.</li>
 *     <li>{@code CONNECTION_ERROR}: Generic connection error.</li>
 *     <li>{@code CONFLICT}: Resource version mismatch.</li>
 *     <li>{@code ALREADY_EXISTS}: The specified resource already exists.</li>
 * </ol>
 * @see Response
*/
public enum ResponseStatuses {

    ///
    OK,
    INTERNAL_ERROR,

    ///..
    NOT_MODIFIED,

    ///..
    UNAUTHENTICATED,
    UNAUTHORIZED,

    ///..
    UNKNOWN_METHOD,
    FORBIDDEN_METHOD,
    UNKNOWN_RESOURCE,
    BAD_FORMATTING,
    VALIDATION_ERROR,
    PAYLOAD_TOO_BIG,

    ///..
    CONNECTION_TIMEOUT,
    BROKEN_STREAM,
    CONNECTION_ERROR,

    ///..
    CONFLICT,
    ALREADY_EXISTS

    ///
}
