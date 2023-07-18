// OK
package io.github.clamentos.blackhole.common.exceptions;

//________________________________________________________________________________________________________________________________________

/**
 * Enumeration of all possible failures.
 * 
 * <ul>
 *     <li>{@code SESSION_NOT_FOUND}: the request specified a non existant session id.</li>
 *     <ul>{@code SESSION_EXPIRED}: the request specified an expired session id.</ul>
 *     <ul>{@code NOT_ENOUGH_PRIVILEGES}: the requesting user doesn't have enough privileges
 *         to perform the desired action.</ul>
 *     <ul>{@code BAD_FORMATTING}: the request was syntactically malformed.</ul>
 *     <ul>{@code IO_ERROR}: generic I/O error.</ul>
 *     <ul>{@code TOO_MANY_CONNECTIONS}: the same ip opened too many sockets.</ul>
 *     <ul>{@code TOO_MANY_REQUESTS}: the socket exhausted the request count.</ul>
 *     <ul>{@code CLIENT_TOO_SLOW}: the client transmitting speed was too low.</ul>
 *     <ul>{@code CLIENT_TIMED_OUT}: the client was inactive for too long.</ul>
 *     <ul>{@code END_OF_STREAM}: end of stream reached.</ul>
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
    END_OF_STREAM;

    //____________________________________________________________________________________________________________________________________
}
