package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.persistence.entities.User;
import io.github.clamentos.blackhole.web.session.UserSession;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * Static class to map objects to and from entities. 
*/
public class EntityMapper {

    //____________________________________________________________________________________________________________________________________

    public static UserSession resultToSession(ResultSet result, int user_id, byte post_permissions) throws SQLException {

        HashMap<Long, Byte> resource_perms = new HashMap<>();
        HashMap<Integer, Byte> user_perms = new HashMap<>();

        if(result.next() == true) {

            resource_perms.put(result.getLong(0), result.getByte(1));
            user_perms.put(result.getInt(2), result.getByte(3));
        }

        return(new UserSession(user_id, post_permissions, user_perms, resource_perms));
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Maps the {@link ResultSet} to a list of {@link User}.
     * @param result : The {@link ResultSet} from the query.
     * @param columns : A checklist of the columns to consider. The positions of the bits
     *                  indicate the index of the column. The LSB is the first column.
     * @return : The never null list of users. If none was mapped, the list will be empty.
     * @throws SQLException If the mapping fails.
    */
    public static List<User> resultToUsers(ResultSet result, int columns) throws SQLException {

        List<User> users = new ArrayList<>();
        User temp;

        while(true) {

            temp = resultToUser(result, columns);

            if(temp != null) {

                users.add(temp);
            }

            else {

                break;
            }
        }

        return(users);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Maps the {@link ResultSet} to a single {@link User}.
     * @param result : The {@link ResultSet} from the query.
     * @param columns : A checklist of the columns to consider. The positions of the bits
     *                  indicate the index of the column. The LSB is the first column.
     * @return : The {@link User}, or {@code null} if there was no mapping.
     * @throws SQLException If the mapping fails.
    */
    public static User resultToUser(ResultSet result, int columns) throws SQLException {

        User user = null;

        if(result.next() == true) {

            user = new User(

                ((columns & 0x00000001) > 0) ? result.getInt(0) : null,
                ((columns & 0x00000002) > 0) ? result.getString(1) : null,
                ((columns & 0x00000004) > 0) ? result.getString(2) : null,
                ((columns & 0x00000008) > 0) ? result.getString(3) : null,
                ((columns & 0x00000010) > 0) ? result.getInt(4) : null,
                ((columns & 0x00000020) > 0) ? result.getInt(5) : null,
                ((columns & 0x00000020) > 0) ? result.getBoolean(6) : null,
                ((columns & 0x00000021) > 0) ? result.getByte(7) : null,
                ((columns & 0x00000020) > 0) ? result.getString(8) : null
            );
        }

        return(user);
    }

    //____________________________________________________________________________________________________________________________________
}
