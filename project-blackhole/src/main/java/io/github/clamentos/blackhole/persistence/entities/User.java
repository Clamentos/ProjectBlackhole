package io.github.clamentos.blackhole.persistence.entities;

import java.util.ArrayList;

import io.github.clamentos.blackhole.persistence.EntityMapper;
import io.github.clamentos.blackhole.web.dtos.Streamable;

/**
 * <p><b>Entity</b></p>
 * User.
*/
public record User(

    Integer id,
    String username,
    String email,
    String password_hash,
    Integer registration_time,
    Integer last_updated,
    Short role_id

) implements Streamable {

    @Override
    public byte[] toBytes() {

        ArrayList<Byte> temp = new ArrayList<>();

        if(id != null) temp.addAll(EntityMapper.numToBytes(id, 4));
        if(username != null) temp.addAll(EntityMapper.stringToList(username));
        if(email != null) temp.addAll(EntityMapper.stringToList(email));
        if(password_hash != null) temp.addAll(EntityMapper.stringToList(password_hash));
        if(registration_time != null) temp.addAll(EntityMapper.numToBytes(registration_time, 4));
        if(last_updated != null) temp.addAll(EntityMapper.numToBytes(last_updated, 4));
        if(role_id != null) temp.addAll(EntityMapper.numToBytes(role_id, 2));

        return(EntityMapper.listToArray(temp));
    }
}
