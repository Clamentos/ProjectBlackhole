package io.github.clamentos.blackhole.persistence.entities;

import io.github.clamentos.blackhole.common.framework.Streamable;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.utility.Converter;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Entity</b></p>
 * The User.
*/
public record User(

    Integer id,
    String username,
    String email,
    String password_hash,
    Integer creation_date,
    Integer last_updated,
    Boolean visible,
    Byte post_permissions,
    String about

) implements Streamable {

    //____________________________________________________________________________________________________________________________________

    @Override
    public byte[] toBytes() {

        ArrayList<Byte> temp = new ArrayList<>();

        if(id != null) temp.addAll(Converter.numToBytes(id, 4));
        if(username != null) temp.addAll(Converter.stringToList(username));
        if(email != null) temp.addAll(Converter.stringToList(email));
        if(password_hash != null) temp.addAll(Converter.stringToList(password_hash));
        if(creation_date != null) temp.addAll(Converter.numToBytes(creation_date, 4));
        if(last_updated != null) temp.addAll(Converter.numToBytes(last_updated, 4));
        if(visible != null) temp.add(visible ? (byte)1 : (byte)0);
        if(post_permissions != null) temp.addAll(Converter.numToBytes(post_permissions, 1));
        if(about != null) temp.addAll(Converter.stringToList(about));

        return(Converter.listToArray(temp));
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Maps the {@link ResultSet} to a list of {@link User}.
     * @param result : The {@link ResultSet} from the query.
     * @param columns : A checklist of the columns to consider. The positions of the bits
     *                  indicate the index of the column. The LSB is the first column.
     * @return The never null list of users. If none was mapped, the list will be empty.
     * @throws SQLException If the mapping fails.
    */
    public static List<User> mapMany(ResultSet result, int columns) throws SQLException {

        ArrayList<User> users = new ArrayList<>();
        User temp;

        while(true) {

            temp = mapSingle(result, columns);

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
     * @return The {@link User}, or {@code null} if there was no mapping.
     * @throws SQLException If the mapping fails.
    */
    public static User mapSingle(ResultSet result, int columns) throws SQLException {

        User user = null;

        if(result.next() == true) {

            user = new User(

                ((columns & 0b0000000001) > 0) ? result.getInt(0) : null,
                ((columns & 0b0000000010) > 0) ? result.getString(1) : null,
                ((columns & 0b0000000100) > 0) ? result.getString(2) : null,
                ((columns & 0b0000001000) > 0) ? result.getString(3) : null,
                ((columns & 0b0000010000) > 0) ? result.getInt(4) : null,
                ((columns & 0b0000100000) > 0) ? result.getInt(5) : null,
                ((columns & 0b0001000000) > 0) ? result.getBoolean(6) : null,
                ((columns & 0b0010000000) > 0) ? result.getByte(7) : null,
                ((columns & 0b0100000000) > 0) ? result.getString(8) : null
            );
        }

        return(user);
    }

    //____________________________________________________________________________________________________________________________________
}
