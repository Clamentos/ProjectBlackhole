package io.github.clamentos.blackhole.framework.implementation.network;

///
import io.github.clamentos.blackhole.framework.implementation.network.tasks.RequestTask;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.DataProvider;

///.
import java.io.IOException;
import java.io.InputStream;

///
/**
 * <h3>Socket Data Provider</h3>
 * Simple data provider wrapper for input streams used while deserializing requests.
 * @see DataProvider
 * @see RequestTask
*/
public final class SocketDataProvider implements DataProvider {

    ///
    /** The input stream to read from. */
    private final InputStream in;

    ///..
    /** The amount of data to provide. */
    private long data_length;

    /** The {@code IOException} instance, if happened. This can be used to detect if {@code this} halted due to an IO related error. */
    private IOException exception;

    ///
    /**
     * Instantiates a new {@link SocketDataProvider} object.
     * @param in : The input stream used as the data source for {@code this}.
     * @param data_length : The size of the data section of the request.
    */
    public SocketDataProvider(InputStream in, long data_length) {

        this.in = in;

        this.data_length = data_length;
        exception = null;
    }

    ///
    /** {@inheritDocs} */
    @Override
    public int fill(byte[] chunk, int starting_position, int amount) {

        int amount_fetched;

        if(exception == null && data_length > 0) {

            try {

                amount_fetched = in.readNBytes(chunk, starting_position, amount);
                data_length -= amount_fetched;

                return(amount_fetched);
            }

            catch(IOException exc) {

                exception = exc;
            }
        }

        return(0);
    }

    ///..
    /**
     * @return <p>The internal {@link IOException}.</p>
     * Can be used to detect if {@code this} halted due to an IO related error.
    */
    public IOException getException() {

        return(exception);
    }

    ///..
    public long getRemainingToProvide() {

        return(data_length);
    }

    ///
}
