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
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ErrorDto;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.RequestHeaders;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ResponseHeaders;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.Task;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;

///..
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DeserializationException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.ValidationException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.servlet.Servlet;
import io.github.clamentos.blackhole.framework.scaffolding.servlet.ServletProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializable;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializer;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.DeserializerProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Methods;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Request;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Resources;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResourcesProvider;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Response;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResponseStatuses;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.validation.Validator;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.validation.ValidatorProvider;

///.
import java.io.IOException;

///..
import java.net.SocketTimeoutException;

///..
import java.util.concurrent.CountDownLatch;

///
/**
 * <h3>Request Task</h3>
 * Responsible for deserializing and dispatching the request to the servlets, as well as sending the response.
 * @see Task
 * @see TransferTask
*/
public final class RequestTask extends Task {

    /** The service used to log notable events. */
    private final Logger logger;

    /** The service used to track application metrics. */
    private final MetricsTracker metrics_service;

    ///..
    /** The application context containing the essential user defined service providers. */
    private final ApplicationContext application_context;

    /** The transfer context containing support objects for the current connection. */
    private final TransferContext transfer_context;

    /** The synchronization primitive used to signal when {@code this} task finishes reading from the input stream. */
    private final CountDownLatch signal;

    /** The length of the data section of the current request. */
    private final long payload_size;

    ///..
    /** The user defined request servlet provider service. */
    private ServletProvider servlet_provider;

    /** The user defined request resources provider service. */
    private ResourcesProvider<? extends Enum<?>> resources_provider;

    /** The user defined request deserializer provider service. */
    private DeserializerProvider deserializer_provider;

    /** The user defined request payload validator provider service. */
    private ValidatorProvider validator_provider;

    ///
    /**
     * Instantiates a new {@link RequestTask} object.
     * @param application_context : The application context from where to get the providers.
     * @param transfer_context : The transfer context holding shared state.
     * @param signal : The primitive used to synchronize while reading from the input stream.
     * @param payload_size : The size of the data section for this request.
     * @throws IllegalArgumentException If either {@code application_context}, {@code transfer_context} or {@code signal} are null.
     * @see ApplicationContext
     * @see TransferContext
     * @see TransferTask
    */
    public RequestTask(ApplicationContext application_context, TransferContext transfer_context, CountDownLatch signal, long payload_size)
    throws IllegalArgumentException {

        if(application_context == null || transfer_context == null || signal == null) {

            throw new IllegalArgumentException("(RequestTask.new) -> The input arguments cannot be null");
        }

        logger = Logger.getInstance();
        metrics_service = MetricsTracker.getInstance();

        this.application_context = application_context;
        this.transfer_context = transfer_context;
        this.signal = signal;
        this.payload_size = payload_size;
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void initialize() {

        if(checkNull(application_context.getServletProvider(), "Servlet provider") == true) return;
        if(checkNull(application_context.getResourcesProvider(), "Resources provider") == true) return;
        if(checkNull(application_context.getDeserializerProvider(), "Deserializer provider") == true) return;
        if(checkNull(application_context.getValidatorProvider(), "Validator provider") == true) return;

        servlet_provider = application_context.getServletProvider();
        resources_provider = application_context.getResourcesProvider();
        deserializer_provider = application_context.getDeserializerProvider();
        validator_provider = application_context.getValidatorProvider();

        transfer_context.active_request_task_count().incrementAndGet();
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void work() {

        RequestHeaders headers = decodeHeaders();
        if(headers == null) return;

        Deserializable dto = null;

        if(headers.payload_size() > 0) {

            dto = deserializeDto(headers);
            if(dto == null) return;
        }

        else {

            signal.countDown();
        }

        if(execute(new NetworkRequest(headers, dto)) == false) return;
    }

    ///.
    /**
     * Checks if the provided object is {@code null} and logs in case it is.
     * @param object : The object to be tested.
     * @param name : The name of the object used for logging.
     * @return {@code true} if {@code object} is {@code null}, {@code false} otherwise.
    */
    private boolean checkNull(Object object, String name) {

        if(object == null) {

            logger.log("RequestTask.checkNull >> " + name + " was null >> Aborting...", LogLevels.ERROR);
            return(true);
        }

        return(false);
    }

    ///..
    /** @return The decoded request headers or {@code null} if any failure occurs. */
    private RequestHeaders decodeHeaders() {

        byte id = 0;
        byte raw_method = 0;
        byte raw_resource = 0;
        byte flags = 0;
        long cache_imestamp = 0;
        byte[] session_id = null;
        Methods method = null;
        Resources<? extends Enum<?>> resource = null;

        try {

            id = transfer_context.in().readByte();
            raw_method = transfer_context.in().readByte();
            raw_resource = transfer_context.in().readByte();
            flags = transfer_context.in().readByte();
            cache_imestamp = transfer_context.in().readLong();

            if((flags & 0b00000001) > 0) {

                session_id = transfer_context.in().readNBytes(32);

                if(session_id.length < 32) {

                    logger.log(

                        "RequestTask.decodeHeaders >> End of stream detected while reading the session id >> Aborting...",
                        LogLevels.ERROR
                    );

                    respondError(id, ResponseStatuses.BROKEN_STREAM, method, "End of stream detected.", false);
                    return(null);
                }
            }

            method = Methods.newInstance(raw_method);
            resource = resources_provider.getResource(raw_resource);

            return(new RequestHeaders(payload_size, id, method, resource, flags, cache_imestamp, session_id));
        }

        catch(IOException | IllegalArgumentException exc) {

            logger.log(

                ExceptionFormatter.format("RequestTask.decodeHeaders >> Could not decode the request headers", exc, ">> Aborting..."),
                LogLevels.ERROR
            );

            if(exc instanceof IllegalArgumentException) {

                if(reposition(payload_size, id, method) == true) {

                    if(resource == null) {

                        respondError(id, ResponseStatuses.UNKNOWN_METHOD, method, "Unknown method with id: " + raw_method, true);
                    }
    
                    else {
    
                        respondError(id, ResponseStatuses.UNKNOWN_RESOURCE, method, "Unknown resource with id: " + raw_resource, true);
                    }
                }
            }

            else {

                if(exc instanceof SocketTimeoutException) {

                    respondError(id, ResponseStatuses.CONNECTION_TIMEOUT, method, "", false);
                }

                else {

                    respondError(id, ResponseStatuses.CONNECTION_ERROR, method, "", false);
                }
            }

            return(null);
        }
    }

    ///..
    /**
     * Deserializes the payload of the incoming request.
     * @param headers : The request headers.
     * @return The decoded payload or {@code null} if any failure occurs.
    */
    private Deserializable deserializeDto(RequestHeaders headers) {

        SocketDataProvider data_provider = new SocketDataProvider(transfer_context.in(), headers.payload_size());

        try {

            // If the deserialization is not "reactive", once it finishes,
            // this task must signal that it's not going to use the input stream anymore.

            // If the deserialization is "reactive", then the task will use the stream for the entire duration of the request.
            // Thus the signal must be sent later (see .execute(...) method).

            Deserializer deserializer = deserializer_provider.getDeserializer(headers.target_resource());

            if(deserializer != null) {

                Deserializable dto = deserializer.deserialize(data_provider, headers.payload_size(), headers.method());

                if(dto.isReactive() == false) {

                    signal.countDown();
                }

                return(dto);
            }

            else {

                logger.log(

                    "RequestTask.deserializeDto >> Deserializer provided for the resource: " +
                    headers.target_resource().toString() + " was null >> Aborting...",
                    LogLevels.ERROR
                );

                if(reposition(headers.payload_size(), headers.id(), headers.method()) == true) {

                    respondError(headers.id(), ResponseStatuses.INTERNAL_ERROR, headers.method(), "", true);
                }

                return(null);
            }
        }

        catch(DeserializationException exc) {

            if(data_provider.getException() != null) {

                // NOTE: Here means that the data provider didn't give the data to the deserializer causing it to throw exception.
                // The reason why this happened is because the data provider itself got an IOException and didn't fill any data.

                logger.log(ExceptionFormatter.format(

                    "RequestTask.deserializeDto >> Could not deserialize", exc,
                    ExceptionFormatter.format(">> Actually caused by >>", data_provider.getException(), ">> Aborting...")

                ), LogLevels.ERROR);

                respondError(headers.id(), ResponseStatuses.CONNECTION_ERROR, headers.method(), "", false);
                return(null);
            }

            else {

                if(reposition(data_provider.getRemainingToProvide(), headers.id(), headers.method()) == true) {

                    respondError(headers.id(), ResponseStatuses.BAD_FORMATTING, headers.method(), exc.getMessage(), true);
                }
            }

            return(null);
        }
    }

    ///..
    /**
     * Validates and dispatches the provided request and also sends the response.
     * @param request : The request to be validated and dispatched.
     * @return {@code true} if no errors were encountered, {@code false} otherwise.
    */
    private boolean execute(Request request) {

        try {

            if(request.getPayload() != null) {

                Validator validator = validator_provider.getValidator(request.getPayload().getClass());

                if(validator != null) {

                    validator.validate(request.getPayload(), request.getMethod());
                }

                else {

                    logger.log(

                        "RequestTask.execute >> Validator provided for the DTO class: " +
                        request.getPayload().getClass().getSimpleName() + " was null >> Aborting...",
                        LogLevels.ERROR
                    );

                    respondError(

                        request.getId(), ResponseStatuses.INTERNAL_ERROR, request.getMethod(), "", !request.getPayload().isReactive()
                    );

                    return(false);
                }
            }

            Servlet servlet = servlet_provider.getServlet(request.getResource());

            if(servlet != null) {

                Response response = servlet.handle(request);

                transfer_context.out_lock().lock();
                response.stream(transfer_context.out());
                transfer_context.out_lock().unlock();

                // If the deserialization done earlier was "reactive" then, at this point,
                // the input stream is guaranteed to not be used anymore. Signal that.

                if(request.getPayload() != null && request.getPayload().isReactive()) {

                    signal.countDown();
                }

                updateMetrics(request.getMethod(), response.getResponseStatus().equals(ResponseStatuses.OK));
                return(true);
            }

            else {

                logger.log(

                    "RequestTask.execute >> Could not find the servlet for the target resource: " +
                    request.getResource().toString(),
                    LogLevels.ERROR
                );

                respondError(request.getId(), ResponseStatuses.INTERNAL_ERROR, request.getMethod(), "", true);
                return(false);
            }
        }

        catch(IOException | ValidationException exc) {

            if(exc instanceof IOException) {

                logger.log(

                    ExceptionFormatter.format("RequestTask.execute >> Could not send the response", exc, ">> Aborting..."),
                    LogLevels.ERROR
                );

                respondError(request.getId(), ResponseStatuses.CONNECTION_ERROR, request.getMethod(), "", false);
            }

            else {

                respondError(request.getId(), ResponseStatuses.VALIDATION_ERROR, request.getMethod(), exc.getMessage(), true);
            }

            return(false);
        }
    }

    ///..
    /**
     * Repositions the stream by skipping the specified number of bytes.
     * @param amount : The number of bytes to skip.
     * @param id : The request id.
     * @param method : The request method.
     * @return {@code true} if no error was encountered, {@code false} otherwise.
    */
    private boolean reposition(long amount, byte id, Methods method) {

        try {

            transfer_context.in().skip(amount);
            return(true);
        }

        catch(IOException exc) {

            logger.log(

                ExceptionFormatter.format("RequestTask.reposition >> Could not reposition the stream", exc, ">> Aborting..."),
                LogLevels.ERROR
            );

            respondError(id, ResponseStatuses.CONNECTION_ERROR, method, "", false);
            return(false);
        }
    }
    
    ///..
    /**
     * Sends an error network response with the given parameters.
     * @param id : The associated request id.
     * @param status : The response status.
     * @param message : The response message.
     * @param method : The request method used to update metrics.
     * @see ResponseStatuses
     * @see Methods
    */
    private void respondError(byte id, ResponseStatuses status, Methods method, String message, boolean recoverable) {

        transfer_context.out_lock().lock();

        try {

            ErrorDto payload = new ErrorDto(message, recoverable);
            new NetworkResponse(new ResponseHeaders(payload.getSize(), id, (byte)0, 0,  status), payload).stream(transfer_context.out());
            metrics_service.incrementResponsesSentOk(1);
        }

        catch(IOException exc) {

            logger.log(ExceptionFormatter.format("RequestTask.respondError >> ", exc, " >> Aborting..."), LogLevels.ERROR);
            metrics_service.incrementResponsesSentKo(1);
        }

        transfer_context.out_lock().unlock();
        transfer_context.active_request_task_count().decrementAndGet();
        signal.countDown();

        updateMetrics(method, false);
    }

    ///..
    /**
     * Updates the request metrics.
     * @param method : The request type.
     * @param ok : {@code true} if status is {@link ResponseStatuses#OK}, {@code false} otherwise.
    */
    private void updateMetrics(Methods method, boolean ok) {

        if(ok == false) {

            switch(method) {

                case null: metrics_service.incrementUnknownRequestsKo(1);
                case CREATE: metrics_service.incrementCreateRequestsKo(1);
                case READ: metrics_service.incrementReadRequestsKo(1);
                case UPDATE: metrics_service.incrementUpdateRequestsKo(1);
                case DELETE: metrics_service.incrementDeleteRequestsKo(1);
            }
        }

        else {

            switch(method) {

                case CREATE: metrics_service.incrementCreateRequestsOk(1);
                case READ: metrics_service.incrementReadRequestsOk(1);
                case UPDATE: metrics_service.incrementUpdateRequestsOk(1);
                case DELETE: metrics_service.incrementDeleteRequestsOk(1);
            }
        }
    }

    ///
}
