package io.github.clamentos.blackhole.web.session;

import java.sql.ResultSet;
import java.util.Map;

public record UserSession(

    int user_id,
    byte post_permissions,
    Map<Integer, Byte> user_permissions,        // permissions towards other user accounts
    Map<Long, Byte> resource_permissions,
    long valid_to

) {

    public static UserSession mapSingle(ResultSet result, int user_id) {

        UserSession session = null;

        if(result.next() == true) {

            session = new UserSession(
                
                //TODO: implement
            )
        }
    }
}