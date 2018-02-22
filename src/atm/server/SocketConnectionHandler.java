package atm.server;

import atm.AccountDto;

import java.io.*;
import java.net.Socket;

public class SocketConnectionHandler extends Thread {
    private Socket socket;

    private ObjectInputStream input;

    private PrintWriter output;

    public SocketConnectionHandler(Socket socket) throws IOException {
        this.socket = socket;
        input = new ObjectInputStream(this.socket.getInputStream());
        output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream())), true);
        start();
    }

    public void run() {
        try {
            Object inObj = input.readObject();
            if (!(inObj instanceof AccountDto)) {
                throw new InvalidObjectException("Expected to receive an " + AccountDto.class.getName());
            }
            AccountDto dto = (AccountDto) inObj;

            System.out.println("Processing operation: " + dto.operation);
            output.write("5020.0");
            output.flush();

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}