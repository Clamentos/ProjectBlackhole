package io.github.clamentos.blackhole.web.servlets;

import io.github.clamentos.blackhole.web.Servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UserServlet implements Servlet {
    
    @Override
    public boolean match(byte resource_in) {

        return(resource_in == 0);
    }

    @Override
    public void handle(DataInputStream input_stream, DataOutputStream output_stream) {

        try {

            byte request_method = input_stream.readByte();

            switch(request_method) {

                //...
            }
        }

        catch(IOException exc) {

            //...
        }
    }
}
