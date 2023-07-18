package io.github.clamentos.blackhole;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.Socket;

import org.junit.jupiter.api.Test;

import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;

public class AppTest {

    /*@Test
    public void launchATest() {
        
        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        Server server = Server.getInstance();
        Thread t = server.start();

        send();

        try {

            t.join();
        }

        catch(InterruptedException exc) {

            System.out.println("interrupted in main");
        }
    }

    private void send() {

        try {

            byte[] data_out = new byte[4 + 1];
            byte[] data_in;
            Socket socket = new Socket("127.0.01", 8080);

            data_out[0] = 0;
            data_out[1] = 0;
            data_out[2] = 0;
            data_out[3] = 1;

            data_out[4] = 69;

            socket.getOutputStream().write(data_out);
            data_in = socket.getInputStream().readNBytes(1);
            socket.close();

            for(Byte elem : data_in) {

                System.out.println("CLIENT: " + elem);
            }
        }

        catch(Exception exc) {

            System.out.println("CLIENT ERROR");
        }
    }*/
}
