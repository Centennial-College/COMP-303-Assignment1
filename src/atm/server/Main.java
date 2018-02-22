package atm.server;

import atm.Constants;
import atm.server.SocketConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Server Starting...");

        try (ServerSocket serverSocket = new ServerSocket(Constants.PORT)) {
            System.out.println("Server socket opened on port " + serverSocket.getLocalPort());

            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    new SocketConnectionHandler(socket);
                } catch (IOException e){
                    System.out.println("Error: " + e.getMessage());

                    if (socket != null && !socket.isClosed())
                        socket.close();
                }
            }
        }
    }
}
