package io.github.clamentos.blackhole.web.session;

import java.util.Map;

public record UserSession(

    int user_id,
    byte post_permissions,
    Map<Integer, Byte> user_permissions,        // permissions towards other user accounts
    Map<Long, Byte> resource_permissions

) {}