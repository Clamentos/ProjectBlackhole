package io.github.clamentos.blackhole;

import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ErrorDto;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.Types;
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DeserializationException;
import io.github.clamentos.blackhole.framework.scaffolding.servlet.Servlet;
import io.github.clamentos.blackhole.framework.scaffolding.servlet.ServletProvider;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.DataProvider;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializable;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializer;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.DeserializerProvider;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Methods;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Request;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Resources;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResourcesProvider;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Response;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResponseFactory;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResponseStatuses;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.validation.ValidatorProvider;

// Testing purposes
public class Prova implements ApplicationContext {

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

    public class Serv implements Servlet {

        @Override
        public Response handle(Request request) {

            try {

                Thread.sleep(1000);
            }

            catch(InterruptedException exc) {

                //...
            }

            ErrorDto dto = (ErrorDto)request.getPayload();
            return(ResponseFactory.build(request.getId(), ResponseStatuses.OK, dto));
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

        throw new UnsupportedOperationException("Unimplemented method 'getValidatorProvider'");
    }
}
