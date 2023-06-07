package io.github.clamentos.blackhole.web.servlets;

import io.github.clamentos.blackhole.web.Servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;

// TODO: logic... and maybe make this singleton?
public class UserServlet implements Servlet {
    
    @Override
    public byte matches() {

        return(1);
    }

    @Override
    public void handle(DataInputStream input_stream, DataOutputStream output_stream, Connection db_connection) {

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
