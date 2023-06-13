package io.github.clamentos.blackhole.web.session;

import io.github.clamentos.blackhole.persistence.entities.EndpointPermission;
import java.util.List;

public record UserSession(

    byte[] session_id,
    List<EndpointPermission> permissions
) {}
