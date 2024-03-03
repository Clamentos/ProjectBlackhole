package io.github.clamentos.blackhole.framework.implementation.network.transfer;

///
import java.io.DataInputStream;
import java.io.DataOutputStream;

///..
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

///..
import java.util.concurrent.locks.Lock;

///
/**
 * <h3>Transfer Context</h3>
 * Holds objects used during request processing.
*/
public final record TransferContext(

    ///
    /** The socket input stream. */
    DataInputStream in,

    /** The socket output stream. */
    DataOutputStream out,

    /** The output stream lock. */
    Lock out_lock,

    /** The number of currently active request tasks. */
    AtomicInteger active_request_task_count,

    /** The unrecoverable error flag. */
    AtomicBoolean unercoverable_error_flag

    ///
) {}
