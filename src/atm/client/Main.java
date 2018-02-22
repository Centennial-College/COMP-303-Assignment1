package atm.client;

import atm.AccountDto;
import atm.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Connecting to server...");

        try (Socket socket = new Socket("localhost", Constants.PORT)) {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            AccountDto dto = new AccountDto();
            dto.operation = "balance";

            output.writeObject(dto);
            String inValue = input.readLine();
            double balance = new Double(inValue);

            System.out.println("Balance is: $" + balance);
        }

    }
}
