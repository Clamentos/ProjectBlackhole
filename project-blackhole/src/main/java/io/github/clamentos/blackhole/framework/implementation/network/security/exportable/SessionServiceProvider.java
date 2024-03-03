package io.github.clamentos.blackhole.framework.implementation.network.security.exportable;

///
import io.github.clamentos.blackhole.framework.implementation.network.security.NetworkSessionService;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.security.SessionService;

///
public final class SessionServiceProvider {

    ///
    public static SessionService getSessionService() {

        return(NetworkSessionService.getInstance());
    }

    ///
}
