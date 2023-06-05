package io.github.clamentos.blackhole.web;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;

public class Dispatcher {

    private Servlet[] servlets;

    public Dispatcher(Servlet[] servlets) {

        this.servlets = servlets;
    }

    public void dispatch(DataInputStream input_stream, DataOutputStream output_stream,  Connection db_connection) throws IOException {

        byte resource_id = input_stream.readByte();

        for(Servlet servlet : servlets) {

            if(servlet.match(resource_id) == true) {

                servlet.handle(input_stream, output_stream);
                break;
            }
        }
    }
}