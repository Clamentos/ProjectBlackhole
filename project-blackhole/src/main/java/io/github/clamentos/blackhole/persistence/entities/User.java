package io.github.clamentos.blackhole.persistence.entities;

import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.web.dtos.Streamable;

import java.util.ArrayList;

/**
 * <p><b>Entity</b></p>
 * User.
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
}
