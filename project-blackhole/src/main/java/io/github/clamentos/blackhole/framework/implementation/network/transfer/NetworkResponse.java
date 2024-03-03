package io.github.clamentos.blackhole.framework.implementation.network.transfer;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.serialization.Streamable;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output.Response;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output.ResponseStatuses;

///..
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ResponseHeaders;

///..
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.exportable.Types;

///.
import java.io.DataOutputStream;
import java.io.IOException;

///
/**
 * <h3>Network Response</h3>
 * This class holds all the fields and data that can be sent through a stream as a network response.
*/
public final record NetworkResponse(

    /** The response headers. */
    ResponseHeaders headers,

    /** The response data payload. */
    Streamable data

) implements Response {

    ///
    /**
     * Instantiates a new {@link NetworkResponse} object.
     * @param headers : The request headers.
     * @param data : The request payload.
     * @throws IllegalArgumentException If {@code headers} is {@code null}.
    */
    public NetworkResponse {

        if(headers == null) {

            throw new IllegalArgumentException("NetworkResponse.new -> The input argument \"headers\" cannot be null");
        }
    }

    ///
    /** {@inheritDoc} */
    @Override
    public long getSize() {

        if(data != null) {

            return(headers.getSize() + data.getSize() + 2);
        }

        else {

            return(headers.getSize() + 2);
        }
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void stream(DataOutputStream out) throws IOException {

        headers.stream(out);

        out.writeByte(Types.BEGIN.ordinal());
        if(data != null) data.stream(out);
        out.writeByte(Types.END.ordinal());
    }

    ///..
    /** {@inheritDocs} */
    @Override
    public ResponseStatuses getResponseStatus() {

        return(headers.response_status());
    }

    ///
}
