package io.github.clamentos.blackhole.framework.implementation.network;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.network.tasks.ServerTask;
import io.github.clamentos.blackhole.framework.implementation.network.tasks.TransferTask;

///.
import java.net.SocketAddress;

///..
import java.util.Map;

///..
import java.util.concurrent.ConcurrentHashMap;

///..
import java.util.concurrent.atomic.AtomicInteger;

///
/**
 * <h3>Server context</h3>
 * Provides state and methods for connection managing.
 * @see ServerTask
 * @see TransferTask
*/
public final class ServerContext {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final ServerContext INSTANCE = new ServerContext();

    ///.
    /** The current number of active client sockets in the system. */
    private final AtomicInteger current_socket_count;

    /** The current number of active sockets per ip mappings. */
    private final Map<SocketAddress, Integer> sockets_per_ip;

    ///
    /**
     * <p>Instantiates a new {@code ServerContext} object.</p>
     * Since this class is a singleton, this constructor will only be called once.
    */
    private ServerContext() {

        current_socket_count = new AtomicInteger(0);
        sockets_per_ip = new ConcurrentHashMap<>();
    }

    ///
    /** @return The {@link ServerContext} instance created during class loading. */
    public static ServerContext getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Checks if the client socket address doesn't exceed the global and per-client count limits.
     * @param socket_address : The target client socket address.
     * @return {@code true} if ok, {@code false} otherwise.
     * @throws NullPointerException If {@code socket_address} is {@code null}.
    */
    public boolean isClientSocketAllowed(SocketAddress socket_address) throws NullPointerException {

        return(

            (sockets_per_ip.getOrDefault(socket_address, 0) < ConfigurationProvider.getInstance().MAX_CLIENTS_PER_IP) &&
            (current_socket_count.get() < ConfigurationProvider.getInstance().MAX_SOCKETS)
        );
    }

    ///..
    /**
     * Increments the counters for the provided client socket address.
     * @param socket_address : The target client socket address.
     * @throws NullPointerException If {@code socket_address} is {@code null}.
    */
    public void increment(SocketAddress socket_address) throws NullPointerException {

        sockets_per_ip.put(socket_address, sockets_per_ip.getOrDefault(socket_address, 0) + 1);
        current_socket_count.incrementAndGet();
    }

    ///..
    /**
     * Decrements the counters for the provided client socket address.
     * @param socket_address : The target client socket address.
     * @throws NullPointerException If {@code socket_address} is {@code null}.
     * @throws IllegalStateException If there is no mapping for {@code socket_address}.
    */
    public void decrement(SocketAddress socket_address) throws NullPointerException, IllegalStateException {

        Integer count = sockets_per_ip.getOrDefault(socket_address, 0);

        // If the client has at least 1 entry, decrement.
        if(count > 0) {

            sockets_per_ip.put(socket_address, count - 1);
            current_socket_count.decrementAndGet();

            // If the initial get was 1 -> decremented to 0 => no need to keep the entry.
            if(count == 1) {

                sockets_per_ip.remove(socket_address);
            }

            return;
        }

        throw new IllegalStateException("(ServerContext.decrement) -> The specified socket address has no entries");
    }

    ///
}
