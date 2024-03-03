package io.github.clamentos.blackhole.framework.implementation.network.security;

import io.github.clamentos.blackhole.framework.scaffolding.network.security.Role;

public final record Session(

    long expiration,
    long user_id,
    Role<?> role

) {}
