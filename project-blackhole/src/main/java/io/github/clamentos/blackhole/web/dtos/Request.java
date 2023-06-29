package io.github.clamentos.blackhole.web.dtos;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.utility.Converter;
import io.github.clamentos.blackhole.web.dtos.components.DataEntry;
import io.github.clamentos.blackhole.web.dtos.components.Method;
import io.github.clamentos.blackhole.web.dtos.components.Entities;
import io.github.clamentos.blackhole.web.dtos.components.Type;

import java.util.ArrayList;
import java.util.List;

//________________________________________________________________________________________________________________________________________

/**
 * Request class.
 * This class holds all the fields and data required to handle a request.
 * The getter methods are all thread safe and standard.
*/
public class Request {

    private Entities resource;
    private Method method;
    private byte[] session_id;
    private List<DataEntry> data;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiates a new {@link Request} object.
     * @param data : The input data array, usually taken from a stream.
     * @throws IllegalArgumentException If the data holds any illegal value.
    */
    public Request(byte[] data) throws IllegalArgumentException {

        Entities resource;
        Method method;
        byte[] session_id;
        List<DataEntry> stuff;
        int start_pos;

        switch(data[0]) {

            case 0: resource = Entities.SYSTEM; break;
            case 1: resource = Entities.USER; break;
            case 2: resource = Entities.TAG; break;
            case 3: resource = Entities.RESOURCE; break;

            default: throw new IllegalArgumentException("Unknown resource type.");
        }

        switch(data[1]) {

            case 0: method = Method.CREATE; break;
            case 1: method = Method.READ; break;
            case 2: method = Method.UPDATE; break;
            case 3: method = Method.DELETE; break;
            case 4: method = Method.LOGIN; break;
            case 5: method = Method.LOGOUT; break;

            default: throw new IllegalArgumentException("Unknown request method.");
        }

        if(method != Method.LOGIN) {

            session_id = new byte[32];

            for(int i = 0; i < session_id.length; i++) {

                session_id[i] = data[i + 2];
            }

            start_pos = 34;
        }

        else {

            session_id = null;
            start_pos = 2;
        }

        stuff = new ArrayList<>();

        while(start_pos < data.length) {

            DataEntry entry = parseEntry(data, start_pos);
            stuff.add(entry);
            start_pos += (1 + (entry.length() == null ? 0 : entry.length()) + entry.data().length);
        }

        this.resource = resource;
        this.method = method;
        this.session_id = session_id;
        this.data = stuff;
    }

    //____________________________________________________________________________________________________________________________________

    public Entities getEntityType() {

        return(resource);
    }

    public Method getMethod() {

        return(method);
    }

    public byte[] getSessionId() {

        return(session_id);
    }

    public List<DataEntry> getData() {

        return(data);
    }

    //____________________________________________________________________________________________________________________________________

    private DataEntry parseEntry(byte[] data, int pos) {

        Type type;
        Integer length;
        byte[] entry_data;
        int start_pos;

        switch(data[pos]) {

            case 0: 
            
                type = Type.BYTE;
                length = 1;
                start_pos = pos + 1;

            break;

            case 1: 
            
                type = Type.SHORT;
                length = 2;
                start_pos = pos + 1;
                
            break;

            case 2: 
            
                type = Type.INT;
                length = 4;
                start_pos = pos + 1;
                
            break;

            case 3: 
            
                type = Type.LONG;
                length = 8;
                start_pos = pos + 1;
                
            break;

            case 4: 
            
                type = Type.FLOAT;
                length = 4;
                start_pos = pos + 1;
                
            break;

            case 5: 
            
                type = Type.DOUBLE;
                length = 8;
                start_pos = pos + 1;
                
            break;

            case 6:
            
                type = Type.STRING;
                length = (int)Converter.bytesToNum(data, pos + 1, 4);
                start_pos = pos + 4;
                
            break;
            case 7:
            
                type = Type.RAW;
                length = (int)Converter.bytesToNum(data, pos + 1, 4);
                start_pos = pos + 4;
            
            break;

            case 8: return(new DataEntry(Type.NULL, null, null));

            default: throw new IllegalArgumentException("Unknown type.");
        }

        entry_data = new byte[length];
        System.arraycopy(data, start_pos, entry_data, 0, entry_data.length);

        return(new DataEntry(type, length, entry_data));
    }

    //____________________________________________________________________________________________________________________________________
}