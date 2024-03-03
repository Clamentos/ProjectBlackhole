package io.github.clamentos.blackhole;

import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.NamedLogger;
import io.github.clamentos.blackhole.framework.implementation.network.security.NetworkSessionService;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.exportable.ErrorDto;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.exportable.Types;
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.ResponseFactory;
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DeserializationException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.ValidationException;
import io.github.clamentos.blackhole.framework.scaffolding.network.controller.Servlet;
import io.github.clamentos.blackhole.framework.scaffolding.network.controller.ServletProvider;
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.DataProvider;
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.Deserializable;
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.Deserializer;
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.DeserializerProvider;
import io.github.clamentos.blackhole.framework.scaffolding.network.security.Role;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Methods;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Request;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Resources;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.ResourcesProvider;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output.Response;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output.ResponseStatuses;
import io.github.clamentos.blackhole.framework.scaffolding.network.validation.Validator;
import io.github.clamentos.blackhole.framework.scaffolding.network.validation.ValidatorProvider;

// Testing purposes, may not work
public class Prova implements ApplicationContext {

    final NamedLogger logger = new NamedLogger();
    NetworkSessionService nss = NetworkSessionService.getInstance();

    public class DeserializerImpl implements Deserializer {

        @Override
        public Deserializable deserialize(DataProvider dp, long payload_size, Methods request_method) throws DeserializationException {

            byte[] data = new byte[(int)payload_size];
            dp.fill(data, 0, (int)payload_size);

            try {

                Types t = Types.newInstance(data[0]);

                if(t.equals(Types.LONG) == false) {

                    //error
                    throw new DeserializationException("...");
                }

                // read timestamp
                long timestamp = (data[1] << 56) | (data[2] << 48) | (data[3] << 40) | (data[4] << 32) | (data[5] << 24) | (data[6] << 16) | (data[7] << 8) | data[8];

                t = Types.newInstance(data[9]);

                if(t.equals(Types.STRING) == false) {

                    //error
                    throw new DeserializationException("...");
                }

                // read string
                int str_len = (data[10] << 24) | (data[11] << 16) | (data[12] << 8) | data[13];
                String str = new String(data, 14, str_len);

                return(new ErrorDto(timestamp, str, true));
            }

            catch(IllegalArgumentException exc) {

                throw new DeserializationException("...");
            }
        }
    }

    private DeserializerImpl d = new DeserializerImpl();

    public class DeserializerProviderImpl implements DeserializerProvider {

        @Override
        public Deserializer getDeserializer(Resources<? extends Enum<?>> resource) {

            logger.log("getDeserializer", "message", LogLevels.INFO);
            return(d);
        }
    }

    private DeserializerProviderImpl dp = new DeserializerProviderImpl();

    public enum Res implements Resources<Res> {

        ECHO
    }

    public class ResP implements ResourcesProvider<Res> {

        @Override
        public Res getResource(byte id) throws IllegalArgumentException {

            switch(id) {

                case 0: return(Res.ECHO);
                default: throw new IllegalArgumentException("ResP::getResource -> No such resource with id: " + id);
            }
        }
    }

    private ResP rp = new ResP();

    public enum RoleI implements Role<RoleI> {

        USER,
        ADMIN
    }

    public class Serv implements Servlet {

        @Override
        public Response handle(Request request) {

            /*try {

                Thread.sleep(1000);
            }

            catch(InterruptedException exc) {

                //...
            }*/

            ErrorDto dto = (ErrorDto)request.getPayload();
            return(ResponseFactory.build(request, ResponseStatuses.OK, (byte)0, 0L, dto));
        }
    }

    private Serv s = new Serv();

    public class ServP implements ServletProvider {

        @Override
        public Servlet getServlet(Resources<? extends Enum<?>> resource) {

            if(resource.equals(Res.ECHO)) {

                return(s);
            }

            else {

                throw new IllegalArgumentException("ServP::getServlet -> No such servlet for resource: " + resource.toString());
            }
        }
    }

    private ServP sp = new ServP();
    
    public class Val implements Validator {

        @Override
        public void validate(Deserializable obj, Methods request_method) throws ValidationException {

            // empty
        }
    }

    private Val v = new Val();


    



    @Override
    public ServletProvider getServletProvider() {

        return(sp);
    }

    @Override
    public ResourcesProvider<? extends Enum<?>> getResourcesProvider() {

        return(rp);
    }

    @Override
    public DeserializerProvider getDeserializerProvider() {

        return(dp);
    }

    @Override
    public ValidatorProvider getValidatorProvider() {

        return((dto) -> {

            return(v);
        });
    }
}
