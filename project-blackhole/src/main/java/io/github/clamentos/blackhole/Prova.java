package io.github.clamentos.blackhole;

import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;
import io.github.clamentos.blackhole.framework.scaffolding.servlet.ServletProvider;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializer;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResourcesProvider;

// Testing purposes
public class Prova implements ApplicationContext {

    @Override
    public ServletProvider getServletProvider() {

        return(null);
    }

    @Override
    public ResourcesProvider<? extends Enum<?>> getResourcesProvider() {

        return(null);
    }

    @Override
    public Deserializer getDeserializer() {

        return(null);
    }
}
