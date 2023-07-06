package io.github.clamentos.blackhole.web.session;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Simple class that holds the user session information.
 * Such information include the id, various permissions
 * and a timestamp to indicate the expiration.</p>
*/
public record UserSession(

    int user_id,
    byte post_permissions,
    Map<Integer, Byte> user_permissions,    // -> permissions towards other user accounts
    Map<Long, Byte> resource_permissions,
    long valid_to

) {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Maps the query {@link ResultSet} and extra information
     * to a single new {@link UserSession} object.
     * @param result : The query result set.
     * @param user_id : The id of the user.
     * @param post_permissions : The post permissions of the user.
     * @return The new {@link UserSession} instance.
     * @throws SQLException If the mapping from the query result set fails.
    */
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

    //____________________________________________________________________________________________________________________________________
}