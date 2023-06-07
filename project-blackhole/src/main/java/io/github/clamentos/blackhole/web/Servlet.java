package io.github.clamentos.blackhole.web;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Connection;

public interface Servlet {
    
    public byte matches();
    public void handle(DataInputStream input_stream, DataOutputStream output_stream, Connection db_connection);
}

/*
 * the protocol:
 * 
 * request shape:
 * 
 *     |resource(1)|method(1)|session_id(32) [optional]|
 *     |data_entry(?)|
 * 
 *     each data_entry is structured:
 * 
 *         |type(1)|length(4) [optional]|data(?)|
 * 
 *     type values:
 * 
 *         0: byte -> length = 1
 *         1: short -> length = 2
 *         2: int -> length = 4
 *         3: long -> length = 8
 *         4: float -> length = 4
 *         5: double -> length = 8
 *         6: string -> length = use length(4)
 *         7: raw -> length = use length(4)
 * 
 *  
 * 
 * response shape:
 * 
 *     |status(1)|data_entry(?)|
 * 
 * 
 * 
 * resource values:
 * 
 *     0: system
 *     1: user
 *     2: resource
 *     3: tag
 *     ...
 * 
 * method values:
 * 
 *     0: create
 *     1: read
 *     2: update
 *     3: delete
 * 
 *     5: login (only when resource value == 0)
 *     ...
*/