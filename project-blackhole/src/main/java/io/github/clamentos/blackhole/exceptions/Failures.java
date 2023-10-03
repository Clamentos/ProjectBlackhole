package io.github.clamentos.blackhole.exceptions;

///
/**
 * <h3>Global failure constants</h3>
 * 
 * <p>This class contains all the error causes that can be encountered within the whole application. The
 * failures range between various categories such as IO, database, formatting, security and other kinds of
 * errors.</p>
 * 
 * <p>Classes can use these constants in exceptions to indicate a precise cause of the failure.
 * This can be used for logging or even directly returned as a response status.</p>
 * 
 * The following is the complete list:
 * <ul>
 *     <li>{@code INACTIVE_CLIENT_SOCKET}: The client's socket was inactive for too long and got closed.</li>
 *     <li>{@code CLIENT_SOCKET_TOO_SLOW}: The client's socket was transmitting too slowly and got closed.</li>
 *     <li>{@code TOO_MANY_CLIENT_SOCKETS}: The client opened too many sockets.</li>
 *     <li>{@code CLIENT_SOCKET_ERROR}: Generic client socket related error.</li>
 *     <li>{@code STREAM_CLOSED}: The client's socket stream(s) were found close.</li>
 *     <li>{@code END_OF_STREAM}: End of stream reached when it shouldn't have.</li>
 *     <li>{@code IO_ERROR}: Generic IO related error.</li>
 *     <li>{@code BAD_FORMATTING}: The client request was ill formed.</li>
 *     <li>{@code UNKNOWN_RESOURCE}: The client's request specified an unknown resource type.</li>
 * 
 *     <li>{@code UNSUPPORTED_METHOD}: The client's request specified an illegal method for the specified
 *         resource type.</li>
 * 
 *     <li>{@code UNKNOWN_METHOD}: The client's request specified an unknown method.</li>
 * 
 *     <li>{@code NOT_ENOUGH_PRIVILEGES}: The client doesn't have enough privileges to perform the desired
 *         action.</li>
 * 
 *     <li>{@code SESSION_NOT_FOUND}: The client's request specified a session id that doesn't exist.</li>
 *     <li>{@code SESSION_EXPIRED}: The client's request specified a session id that is expired.</li>
 *     <li>{@code TOO_MANY_SESSIONS}: The user exceeded the maximum number of allowed sessions.</li>
 *     <li>{@code DB_RETRIES_EXHAUSTED}: Database connection retries were exceeded.</li>
 *     <li>{@code NULL_DB_RESULT_SET}: Database returned a null result set.</li>
 *     <li>{@code ERROR}: Generic error.</li>
 * </ul>
*/
public enum Failures {

    ///
    INACTIVE_CLIENT_SOCKET,
    CLIENT_SOCKET_TOO_SLOW,
    TOO_MANY_CLIENT_SOCKETS,
    CLIENT_SOCKET_ERROR,

    STREAM_CLOSED,
    END_OF_STREAM,
    IO_ERROR,

    ///
    BAD_FORMATTING,
    UNKNOWN_RESOURCE,
    UNSUPPORTED_METHOD,
    UNKNOWN_METHOD,

    ///
    NOT_ENOUGH_PRIVILEGES,
    SESSION_NOT_FOUND,
    SESSION_EXPIRED,
    TOO_MANY_SESSIONS,

    ///
    DB_RETRIES_EXHAUSTED,
    NULL_DB_RESULT_SET,

    ///
    ERROR;

    ///
}
