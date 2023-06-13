package io.github.clamentos.blackhole.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.clamentos.blackhole.persistence.entities.EndpointPermission;
import io.github.clamentos.blackhole.persistence.entities.User;

public class EntityMapper {
    
    public static List<User> resultToUsers(ResultSet result, int columns) throws SQLException {

        List<User> users = new ArrayList<>();

        while(result.next() == true) {

            users.add(new User(

                ((columns & 0x00000001) > 0) ? result.getInt(0) : 0,
                ((columns & 0x00000002) > 0) ? result.getString(1) : null,
                ((columns & 0x00000004) > 0) ? result.getString(2) : null,
                ((columns & 0x00000008) > 0) ? result.getString(3) : null,
                ((columns & 0x00000010) > 0) ? result.getInt(4) : 0,
                ((columns & 0x00000020) > 0) ? result.getInt(5) : 0
            ));
        }

        return(users);
    }

    public static User resultToUser(ResultSet result, int columns) throws SQLException {

        User user = null;

        while(result.next() == true) {

            user = new User(

                ((columns & 0x00000001) > 0) ? result.getInt(0) : 0,
                ((columns & 0x00000002) > 0) ? result.getString(1) : null,
                ((columns & 0x00000004) > 0) ? result.getString(2) : null,
                ((columns & 0x00000008) > 0) ? result.getString(3) : null,
                ((columns & 0x00000010) > 0) ? result.getInt(4) : 0,
                ((columns & 0x00000020) > 0) ? result.getInt(5) : 0
            );
        }

        return(user);
    }

    public static List<EndpointPermission> resultToEndpointPermissions(ResultSet result, int columns) throws SQLException {

        List<EndpointPermission> perms = new ArrayList<>();

        while(result.next() == true) {

            perms.add(new EndpointPermission(

                ((columns & 0x00000001) > 0) ? result.getLong(0) : 0,
                ((columns & 0x00000002) > 0) ? result.getInt(1) : 0,
                ((columns & 0x00000004) > 0) ? result.getShort(2) : 0,
                ((columns & 0x00000008) > 0) ? result.getShort(3) : 0
            ));
        }

        return(perms);
    }
}
