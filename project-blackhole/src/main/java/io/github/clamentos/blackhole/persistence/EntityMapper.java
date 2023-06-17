package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.persistence.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//________________________________________________________________________________________________________________________________________

/**
 * Static class to map objects to and from entities. 
*/
public class EntityMapper {

    //____________________________________________________________________________________________________________________________________

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

                ((columns & 0x00000001) > 0) ? result.getInt(0) : 0,
                ((columns & 0x00000002) > 0) ? result.getString(1) : null,
                ((columns & 0x00000004) > 0) ? result.getString(2) : null,
                ((columns & 0x00000008) > 0) ? result.getString(3) : null,
                ((columns & 0x00000010) > 0) ? result.getInt(4) : 0,
                ((columns & 0x00000020) > 0) ? result.getInt(5) : 0,
                ((columns & 0x00000021) > 0) ? result.getShort(6) : 0
            );
        }

        return(user);
    }

    public static Map<Long, Byte> resultToPermMap(ResultSet result) throws SQLException {

        Map<Long, Byte> perms = new HashMap<>();
        
        while(result.next() == true) {

            perms.put(result.getLong(0), result.getByte(1));
        }

        return(perms);
    }

    //____________________________________________________________________________________________________________________________________

    public static List<Byte> numToBytes(long number, int length) {

        List<Byte> result = new ArrayList<>();
        
        for(int i = 0; i < length; i++) {

            result.add((byte)(number & (255 << i * 8) >> i * 8));
        }

        return(result);
    }

    public static byte[] listToArray(List<Byte> list) {

        byte[] result;

        if(list == null) {

            return(new byte[0]);
        }

        result = new byte[list.size()];

        for(int i = 0; i < list.size(); i++) {

            result[i] = list.get(i);
        }

        return(result);
    }

    public static List<Byte> stringToList(String str) {

        ArrayList<Byte> result = new ArrayList<>();

        for(Byte ch : str.getBytes()) {

            result.add(ch);
        }

        return(result);
    }

    //____________________________________________________________________________________________________________________________________
}
