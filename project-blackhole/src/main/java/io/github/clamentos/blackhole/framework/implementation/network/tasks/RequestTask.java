package io.github.clamentos.blackhole.framework.implementation.network.tasks;

///
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;
import io.github.clamentos.blackhole.framework.implementation.logging.MetricsTracker;

///..
import io.github.clamentos.blackhole.framework.implementation.network.SocketDataProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkRequest;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkResponse;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.TransferContext;

///..
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.RequestHeaders;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ResponseHeaders;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ResponseStatuses;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.SimpleDto;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.Task;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;
import io.github.clamentos.blackhole.framework.implementation.utility.StreamUtils;

///..
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DeserializationException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.servlet.Servlet;
import io.github.clamentos.blackhole.framework.scaffolding.servlet.ServletProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializer;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.DataTransferObject;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Methods;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Request;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Resources;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResourcesProvider;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Response;

///.
import java.io.IOException;

///..
import java.util.NoSuchElementException;

///..
import java.util.concurrent.CountDownLatch;

///
/**
 * <h3>Request task</h3>
 * Responsible for deserializing and dispatching the request as well as sending the response.
 * @see Task
*/
public final class RequestTask extends Task {

    /** The service used to log notable events. */
    private final Logger logger;

    /** The service used to track application metrics. */
    private final MetricsTracker metrics_service;

    ///..
    /** The application context containing the essential user-defined service providers. */
    private final ApplicationContext application_context;

    /** The transfer context containing support objects for the current connection. */
    private final TransferContext transfer_context;

    /** The synchronization primitive used to signal when {@code this} task finishes reading from the input stream. */
    private final CountDownLatch signal;

    /** The length of the data section of the current request. */
    private final long data_length;

    ///..
    /** The user-defined request servlet provider service. */
    private ServletProvider servlet_provider;

    /** The user-defined request resources provider service. */
    private ResourcesProvider<? extends Enum<?>> resources_provider;

    /** The user-defined request deserializer service. */
    private Deserializer deserializer;

    ///
    /**
     * Instantiates a new {@link RequestTask} object.
     * @param application_context : The application context from where to get the providers.
     * @param transfer_context : The transfer context holding shared state.
     * @param signal : The primitive used to synchronize reading from the input stream.
     * @param data_length : The size of the data section for this request.
     * @throws IllegalArgumentException If either {@code application_context}, {@code transfer_context} or {@code signal} are null.
     * @see ApplicationContext
     * @see TransferContext
    */
    public RequestTask(ApplicationContext application_context, TransferContext transfer_context, CountDownLatch signal, long data_length) throws IllegalArgumentException {

        if(application_context == null || transfer_context == null || signal == null) {

            throw new IllegalArgumentException("(RequestTask.new) -> The input arguments cannot be null");
        }

        logger = Logger.getInstance();
        metrics_service = MetricsTracker.getInstance();

        this.application_context = application_context;
        this.transfer_context = transfer_context;
        this.signal = signal;
        this.data_length = data_length;
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void initialize() {

        // Get and check all the services from the context.
        if(checkNull(application_context.getServletProvider(), "Servlet provider") == true) return;
        if(checkNull(application_context.getResourcesProvider(), "Resources provider") == true) return;
        if(checkNull(application_context.getDeserializer(), "Deserializer") == true) return;

        // Officially active.
        transfer_context.active_request_task_count().incrementAndGet();
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void work() {

        // TODO: check proper size

        RequestHeaders headers = decodeHeaders();
        if(headers == null) return;

        DataTransferObject dto = deserializeDto(headers.id());
        if(dto == null) return;

        if(execute(new NetworkRequest(headers, dto)) == false) return;
    }

    ///.
    // Checks if an object is null and log.
    private boolean checkNull(Object object, String name) {

        if(object == null) {

            logger.log("RequestTask.checkNull >> " + name + " was null. Aborting...", LogLevels.ERROR);
            return(true);
        }

        return(false);
    }

    ///..
    // Decodes the incoming request headers.
    private RequestHeaders decodeHeaders() {

        byte id = 0;
        byte flags = 0;
        byte raw_method = 0;
        byte raw_resource = 0;
        byte[] session_id = null;

        Methods method = null;
        Resources<? extends Enum<?>> resource = null;

        try {

            // Read the raw header bytes from the input stream.
            id = StreamUtils.readByte(transfer_context.in());
            flags = StreamUtils.readByte(transfer_context.in());
            raw_method = StreamUtils.readByte(transfer_context.in());
            raw_resource = StreamUtils.readByte(transfer_context.in());

            // Parse the method and target resource.
            method = Methods.newInstance(raw_method);
            resource = resources_provider.getResource(raw_resource);

            // If the method is not login, read the session id from the input stream.
            if(method.equals(Methods.LOGIN) == false) {

                session_id = StreamUtils.readBytes(transfer_context.in(), 32);
            }

            return(new RequestHeaders(id, flags, method, resource, session_id));
        }

        catch(IOException | NoSuchElementException | IllegalArgumentException exc) {

            if(exc instanceof IOException || exc instanceof NoSuchElementException) {

                // Raw network error. This situation is unrecoverable, abort (socket likely to be closed).
                logger.log(ExceptionFormatter.format("RequestTask.work >> ", exc, " >> Aborting..."), LogLevels.ERROR);
                free();
            }

            else {

                if(resource == null) {

                    // Parsing of the method failed.
                    respondError(id, ResponseStatuses.UNKNOWN_METHOD, "Unknown request method id: " + raw_method);
                }

                else {

                    // Parsing of the target resource failed.
                    respondError(id, ResponseStatuses.UNKNOWN_RESOURCE, "Unknown target resource id: " + raw_resource);
                }
            }

            return(null);
        }
    }

    ///..
    // Deserializes the incoming data.
    private DataTransferObject deserializeDto(byte id) {

        DataTransferObject dto = null;
        SocketDataProvider data_provider = new SocketDataProvider(transfer_context.in(), data_length);

        try {

            // Call the user-defined deserialized.
            dto = deserializer.deserialize(data_provider);

            // If the serializer is not "reactive", once it finishes,
            // this task must signal that it's not going to use the input stream anymore.

            // If the serializer is "reactive", then the task will use the stream for the entire duration of the request.
            // Thus the signal must be sent later (see .execute method).

            if(deserializer.isReactive() == false) {

                signal.countDown();
            }

            return(dto);
        }

        catch(DeserializationException exc) {

            if(data_provider.getException() != null) {

                // Here means that the data provider didn't give the data to the user-defined deserializer causing it to throw exception.
                // The reason why this happened is because the data provider itself got an IOException and didn't fill any data.
                // This situation is unrecoverable, abort (socket likely to be closed).

                logger.log(

                    ExceptionFormatter.format(
                        
                        "RequestTask.work >> ", exc,
                        ExceptionFormatter.format(" >> Actually caused by >> ", data_provider.getException(), " >> Aborting...")
                    ),
                    LogLevels.ERROR
                );

                free();
                return(null);
            }

            // Bad data.
            respondError(id, ResponseStatuses.BAD_FORMATTING, exc.getResponseMessage());
            return(null);
        }
    }

    ///..
    // Executes the received request and sends the response.
    private boolean execute(Request request) {

        try {

            // Get the matching user-defined servlet to dispatch to.
            Servlet servlet = servlet_provider.getServlet(request.getResource());

            if(servlet != null) {

                // Dispatch the request.
                Response response = servlet.handle(request);

                // Grab the output stream in mutual exclusion and send the response.
                transfer_context.out_lock().lock();
                response.stream(transfer_context.out());
                transfer_context.out_lock().unlock();

                // If the deserializer used earlier was "reactive" then, at this point,
                // the input stream is guaranteed to not be used anymore. Signal that.

                if(deserializer.isReactive()) {

                    signal.countDown();
                }

                return(true);
            }

            else {

                // No servlet found to dispatch to.
                logger.log(

                    "RequestTask.work >> Could not find servlet for target resource: " + request.getResource().toString(), LogLevels.ERROR
                );

                free();
                return(false);
            }
        }

        catch(IOException exc) {

            // Raw network error. This situation is unrecoverable, abort (socket likely to be closed).
            logger.log(ExceptionFormatter.format("RequestTask.work >> ", exc, " >> Aborting..."), LogLevels.ERROR);
            free();

            return(false);
        }
    }

    ///..
    // Constructs and sends a simple error response.
    private void respondError(byte id, ResponseStatuses status, String message) {

        transfer_context.out_lock().lock();

        try {

            new NetworkResponse(new ResponseHeaders(id, (byte)0, status), new SimpleDto(message)).stream(transfer_context.out());
        }

        catch(IOException exc) {

            logger.log(ExceptionFormatter.format("RequestTask.respondError >> ", exc, " >> Aborting..."), LogLevels.ERROR);
        }

        transfer_context.out_lock().unlock();
        metrics_service.incrementReadRequestsKo(1);
        free();
    }

    ///..
    // Updates the contexts.
    private void free() {

        transfer_context.active_request_task_count().decrementAndGet();
        signal.countDown();
    }

    ///
}
