package io.github.clamentos.blackhole.framework.implementation.utility;

///
import java.util.Iterator;
import java.util.Queue;

///..
import java.util.concurrent.ConcurrentLinkedQueue;

///..
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

///..
import java.util.concurrent.locks.LockSupport;

///
/**
 * <h3>Gate</h3>
 * <p>Provides synchronization mechanisms for {@code master -> workers} scenarios.</p>
 * The typical usage of this class is to allow many workers to use a shared resource as long as it's "enabled".
 * The resource can then be locked, allowing only a single "master" thread to operate on the resource.
*/
public class Gate {

    ///
    private final AtomicInteger workers_in_gate_count;
    private final AtomicBoolean is_locked_for_workers;

    ///..
    private final Queue<Thread> parked_workers;
    private volatile Thread master;

    ///
    /**
     * Instantiates a new {@code Gate} object.
     * @param open : The initial status of the gate.
    */
    public Gate(boolean open) {

        workers_in_gate_count = new AtomicInteger(0);
        is_locked_for_workers = new AtomicBoolean(open);

        parked_workers = new ConcurrentLinkedQueue<>();
    }

    ///
    /** Attempts to enter the gate if open, waiting until it is. */
    public void enter() {

        // Wait for the gate to open.
        while(is_locked_for_workers.get() == true) {

            parked_workers.offer(Thread.currentThread());
            LockSupport.park();
        }

        // Enter the gate.
        workers_in_gate_count.incrementAndGet();
    }

    ///..
    /** Exits the gate. */
    public void exit() {

        workers_in_gate_count.decrementAndGet();
        LockSupport.unpark(master);
    }

    ///..
    /**
     * Opens the gate. If the gate is already opened, do nothing.
     * @throws IllegalStateException If {@code this.master} is null.
     * @throws IllegalCallerException If the caller is not a {@code master} thread.
    */
    public synchronized void open() throws IllegalStateException, IllegalCallerException {

        // Only open if the gate is closed, else do nothing.
        if(is_locked_for_workers.get() == true) {

            checkMaster();

            // Unlock the gate.
            is_locked_for_workers.set(false);

            // Unpark all workers.
            Iterator<Thread> workers_iterator = parked_workers.iterator();

            while(workers_iterator.hasNext()) {

                LockSupport.unpark(workers_iterator.next());
                workers_iterator.remove();
            }   
        }
    }

    ///..
    /**
     * <p>Waits for all other threads to exit the gate, then closes the gate.</p>
     * If the gate is already closed, this method does nothing.
     * @throws IllegalStateException If {@code this.master} is null.
     * @throws IllegalCallerException If the caller is not a {@code master} thread.
    */
    public void close() throws IllegalStateException, IllegalCallerException {

        synchronized(this) {

            // If the gate is already closed, simply return.
            if(is_locked_for_workers.get() == true) {

                return;
            }

            checkMaster();

            // Close the gate.
            is_locked_for_workers.set(true);
        }

        // Wait for all to leave.
        while(workers_in_gate_count.get() > 0) {

            LockSupport.park();
        }
    }

    ///..
    /**
     * <p>Sets the {@code this.master} thread.</p>
     * @param master : The target master thread.
     * @throws IllegalStateException If {@code this.master} is already set.
     * @throws IllegalArgumentException If {@code master} is {@code null}.
    */
    public synchronized void setMaster(Thread master) throws IllegalStateException, IllegalArgumentException {

        if(this.master == null) {

            if(master == null) {

                throw new IllegalArgumentException("(Gate.setMaster) -> Input arguments cannot be null");
            }

            this.master = master;
        }

        else {

            throw new IllegalStateException("(Gate.setMaster) -> Master is already set");
        }
    }

    ///
    // Checks if the master is not null and if the caller is not a worker.
    private void checkMaster() throws IllegalStateException {

        if(master == null) {

            throw new IllegalStateException("(Gate.checkMaster) -> Master cannot be null");
        }

        if(Thread.currentThread() != master) {

            throw new IllegalCallerException("(Gate.checkMaster) -> Workers cannot call this method");
        }
    }

    ///
}
