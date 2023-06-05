package io.github.clamentos.blackhole.web;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Servlet {
    
    public boolean match(byte resource_id);
    public void handle(DataInputStream input_stream, DataOutputStream output_stream);
}
