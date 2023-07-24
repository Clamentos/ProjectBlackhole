package io.github.clamentos.blackhole.framework.web;

import io.github.clamentos.blackhole.framework.web.request.Request;
import io.github.clamentos.blackhole.framework.web.request.Response;
import io.github.clamentos.blackhole.framework.web.request.components.Resources;

public interface Servlet {
    
    Resources manages();
    Response handle(Request request);
}
