package io.github.clamentos.blackhole.web.session;

public record UserSession(

    byte[] session_id,
    String user             // dummy field, just to have a value
) {}
