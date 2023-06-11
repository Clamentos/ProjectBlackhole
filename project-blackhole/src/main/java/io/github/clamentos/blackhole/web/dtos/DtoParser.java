package io.github.clamentos.blackhole.web.dtos;

//________________________________________________________________________________________________________________________________________

import java.io.DataInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

public class DtoParser {

    private static volatile DtoParser INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the DtoParser instance.
     * @return The DtoParser instance.
     */
    public static DtoParser getInstance() {

        DtoParser temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new DtoParser();
            }

            lock.unlock();
        }

        return(temp);
    }
    
    public Request parseRequest(DataInputStream input_stream) throws IOException {

        Method request_method;
        byte[] session_id;
        ArrayList<DataEntry> data_entries;

        switch(input_stream.readByte()) {

            case 0: request_method = Method.CREATE; break;
            case 1: request_method = Method.READ; break;
            case 2: request_method = Method.UPDATE; break;
            case 3: request_method = Method.DELETE; break;
            case 4: request_method = Method.LOGIN; break;

            default: throw new IllegalArgumentException("Unknown method");
        }

        if(request_method.equals(Method.LOGIN) == true) {

            session_id = null;
        }

        else {

            session_id = new byte[32];
                
            for(int i = 0; i < 32; i++) {

                session_id[i] = input_stream.readByte();
            }
        }

        data_entries = new ArrayList<>();
            
        while(input_stream.available() > 0) {

            data_entries.add(readDataEntry(input_stream));
        }

        return(new Request(request_method, session_id, data_entries));
    }

    //____________________________________________________________________________________________________________________________________

    private DataEntry readDataEntry(DataInputStream input_stream) throws IOException {

        Type type;
        Integer length;
        byte[] data;

        switch(input_stream.readByte()) {

            case 0:
                
                type = Type.BYTE;
                length = null;
                data = readData(1, input_stream);
                    
                break;
                
            case 1:
                
                type = Type.SHORT;
                length = null;
                data = readData(2, input_stream);
                    
                break;
                
            case 2:
                
                type = Type.INT;
                length = null;
                data = readData(4, input_stream);
                    
                break;
                
            case 3:
                
                type = Type.LONG;
                length = null;
                data = readData(8, input_stream);
                    
                break;
                
            case 4:
                
                type = Type.FLOAT;
                length = null;
                data = readData(4, input_stream);
                    
                break;
                
            case 5:
                
                type = Type.DOUBLE;
                length = null;
                data = readData(8, input_stream);
                    
                break;
                
            case 6:
                
                type = Type.STRING;
                length = input_stream.readInt();
                data = readData(length, input_stream);
                    
                break;
                
            case 7:
                
                type = Type.RAW;
                length = input_stream.readInt();
                data = readData(length, input_stream);
                    
                break;

            default: throw new IllegalArgumentException("Unknown type");
        }

        return(new DataEntry(type, length, data));
    }

    private byte[] readData(int length, DataInputStream input_stream) throws IOException {

        byte[] result = new byte[length];

        for(int i = 0; i < length; i++) {

            result[i] = input_stream.readByte();
        }

        return(result);
    }

    //____________________________________________________________________________________________________________________________________
}
