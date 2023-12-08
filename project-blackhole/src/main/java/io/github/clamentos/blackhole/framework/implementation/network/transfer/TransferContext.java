package io.github.clamentos.blackhole.framework.implementation.network.transfer;

///
import java.io.InputStream;
import java.io.OutputStream;

///..
import java.util.concurrent.atomic.AtomicInteger;

///..
import java.util.concurrent.locks.Lock;

///
/**
 * <h3>Transfer context</h3>
 * Holds objects used during request processing.
*/
public final record TransferContext(

    ///
    /** The socket input stream. */
    InputStream in,

    /** The socket output stream. */
    OutputStream out,

    /** The output stream lock. */
    Lock out_lock,

    /** The number of currently active request tasks. */
    AtomicInteger active_request_task_count

    ///
) {}
