package io.github.clamentos.blackhole.persistence.entities;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Reducible;
import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Type;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Entity</b></p>
 * This class corresponds to the <b>Users</b> entity in the database.
 * The order of the fields must match the db schema.
 * <ol>
 *     <li>{@code Integer id}: unique, not null</li>
 *     <li>{@code String username}: unique, not null, max 32 long</li>
 *     <li>{@code String email}: unique, not null, max 64 long</li>
 *     <li>{@code String password_hash}: not null, max 128 long</li>
 *     <li>{@code Integer creation_date}: not null</li>
 *     <li>{@code Integer last_updated}: not null</li>
 *     <li>{@code Byte post_permissions}: not null</li>
 *     <li>{@code String about}: max 1024 long</li>
 *     <li>{@code Byte flags}: not null</li>
 * </ol>
 * The column {@link User#flags} is a small list of account flags:
 * <ol>
 *     <li>{@code 0b00000001}: enabled</li>
 *     <li>{@code 0b00000010}: expired</li>
 *     <li>{@code 0b00000100}: password expired</li>
 *     <li>{@code 0b00001000}: private</li>
 * </ol>
*/
public record User(

    Integer id,
    String username,
    String email,
    String password_hash,
    Integer creation_date,
    Integer last_updated,
    Byte post_permissions,
    String about,
    Byte flags

) implements Reducible {

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public List<DataEntry> reduce() {

        List<DataEntry> result = new ArrayList<>();

        result.add(id == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, id));
        result.add(username == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.STRING, username));
        result.add(email == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.STRING, email));
        result.add(creation_date == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, creation_date));
        result.add(last_updated == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.INT, last_updated));
        result.add(post_permissions == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.BYTE, post_permissions));
        result.add(about == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.STRING, about));
        result.add(flags == null ? new DataEntry(Type.NULL, null) : new DataEntry(Type.BYTE, flags));

        return(result);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Deserialize the list of {@link DataEntry} in a list of {@link User}.
     * @param entries : The list of {@link DataEntry}.
     * @return The never null list of {@link User}.
     * @throw IllegalArgumentException If the input list is null, empty
     *        or if a {@link DataEntry} is not of correct type.
    */
    public static List<User> deserialize(List<DataEntry> entries) throws IllegalArgumentException {

        ArrayList<User> users = new ArrayList<>();
        int fields;
        int i;

        Integer id;
        String username;
        String email;
        String password_hash;
        Integer creation_date;
        Integer last_updated;
        Byte post_permissions;
        String about;
        Byte flags;

        if(entries == null || entries.size() == 0) {

            throw new IllegalArgumentException("User list cannot be null nor empty");
        }

        i = 0;

        while(i < entries.size()) {

            fields = Converter.entryToInt(entries.get(i));
            i++;

            id = null;
            username = null;
            email = null;
            password_hash = null;
            creation_date = null;
            last_updated = null;
            post_permissions = null;
            about = null;
            flags = null;

            if((fields & 0b0000000001) > 0) {id = Converter.entryToInt(entries.get(i)); i++;}
            if((fields & 0b0000000010) > 0) {username = Converter.entryToString(entries.get(i)); i++;}
            if((fields & 0b0000000100) > 0) {email = Converter.entryToString(entries.get(i)); i++;}
            if((fields & 0b0000001000) > 0) {password_hash = Converter.entryToString(entries.get(i)); i++;}
            if((fields & 0b0000010000) > 0) {creation_date = Converter.entryToInt(entries.get(i)); i++;}
            if((fields & 0b0000100000) > 0) {last_updated = Converter.entryToInt(entries.get(i)); i++;}
            if((fields & 0b0001000000) > 0) {post_permissions = Converter.entryToByte(entries.get(i)); i++;}
            if((fields & 0b0010000000) > 0) {about = Converter.entryToString(entries.get(i)); i++;}
            if((fields & 0b0100000000) > 0) {flags = Converter.entryToByte(entries.get(i)); i++;}

            users.add(new User(

                id,
                username,
                email,
                password_hash,
                creation_date,
                last_updated,
                post_permissions,
                about,
                flags
            ));
        }

        return(users);
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
                ((columns & 0b0001000000) > 0) ? result.getByte(6) : null,
                ((columns & 0b0010000000) > 0) ? result.getString(7) : null,
                ((columns & 0b0100000000) > 0) ? result.getByte(8) : null
            );
        }

        return(user);
    }

    //____________________________________________________________________________________________________________________________________
}
