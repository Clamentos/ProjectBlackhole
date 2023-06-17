package io.github.clamentos.blackhole.web.session;

import java.util.Map;

public record UserSession(

    int user_id,
    short role_id,
    Map<Long, Byte> user_resource_perms

) {}

/*
 * byte flags:
 * 
 * 0000000x -> read
 * 000000x0 -> update
 * 00000x00 -> delete
*/