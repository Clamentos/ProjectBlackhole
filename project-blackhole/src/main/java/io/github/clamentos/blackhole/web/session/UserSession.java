package io.github.clamentos.blackhole.web.session;

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

public record UserSession(

    int user_id,
    byte post_permissions,
    Map<Integer, Byte> user_permissions,        // permissions towards other user accounts
    Map<Long, Byte> resource_permissions,
    long valid_to

) {

    public static UserSession mapSingle(ResultSet result, int user_id, byte post_permissions) throws SQLException {

        HashMap<Integer, Byte> user_perms = new HashMap<>();
        HashMap<Long, Byte> resource_perms = new HashMap<>();

        if(result.next() == true) {

            user_perms.put(result.getInt(0), result.getByte(1));
            resource_perms.put(result.getLong(2), result.getByte(3));
        }

        return(new UserSession(

            user_id,
            post_permissions,
            user_perms,
            resource_perms,
            System.currentTimeMillis() + ConfigurationProvider.SESSION_DURATION
        ));
    }
}