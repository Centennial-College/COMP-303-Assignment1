package atm.server;

import atm.Constants;

import java.io.*;
import java.net.*;

public class MyMultiServer {
    public static void main(String[] args)
            throws IOException {
        System.out.println("Server Starting...");
        try (ServerSocket s = new ServerSocket(Constants.PORT)) {
            while (true) {
                // Blocks until a connection occurs:
                Socket socket = s.accept();
                try {
                    new MyServerOne(socket);
                } catch (IOException e) {
                    // If it fails, close the socket,
                    // otherwise the thread will close it:
                    socket.close();
                }
            }
        }
    }
}