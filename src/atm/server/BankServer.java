package atm.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import atm.client.ClientRequest;
import atm.shared.Constants;

public class BankServer extends Application {
	private static int port;
	private BankDatabase db = new BankDatabase();
	Stage window;
	TextArea messages = new TextArea();

	// public BankServer(int port) {
	// this.port = port;
	// this.db = new BankDatabase();
	// }

	@Override
	public void start(Stage primaryStage) {

		window = primaryStage;
		messages.setPrefHeight(550);
		messages.setEditable(false);
		VBox box = new VBox(10, messages);
		box.setPrefSize(500, 600);

		Scene main = new Scene(box);
		window.setScene(main);
		primaryStage.setTitle("KOPS Bank - Server");
		primaryStage.show();

		try {
			ServerSocket serverSocket = new ServerSocket(port);
			messages.appendText(String.format("Server is listening on port: %s%n", port));

			// while(true) {
			Socket socket = serverSocket.accept();
			new Thread(new ConnectionHandler(socket, this.db)).start();
			messages.appendText(String.format("New ATM Client connected.%n"));
			// }
		} catch (IOException ioe) {
			System.err.printf("IOException: %s%n", ioe);
			ioe.printStackTrace();
		}

	}

	private class ConnectionHandler implements Runnable {
		private Socket socket;
		private BankDatabase db;

		public ConnectionHandler(Socket socket, BankDatabase db) {
			this.socket = socket;
			this.db = db;
		}

		@Override
		public void run() {
			try {
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

				ServerResponse res;
				ClientRequest req;

				// 1. receive obj request
				while ((req = (ClientRequest) in.readObject()) != null) {

					res = new ServerResponse();

					messages.appendText(String.format("CLIENT >>\n" + req));
					messages.appendText(String.format("Successfully received obj\n\n"));

					// 2. process client request
					// 3. send server response
					switch (req.getOperation()) {
					case AUTHENTICATE:
						res.setOperation(req.getOperation());
						res.setOperationSuccess(this.db.authenticateCustomer(req.getCustomerId(), req.getPin()));
						if (!res.isOperationSuccess())
							res.setErrorMessage("You have entered an invalid customer Id or PIN.\nPlease try again!");
						out.writeObject(res);
						break;
					case BALANCE_INQUIRY:
						res.setOperation(req.getOperation());
						res.setOperationSuccess(true);
						res.setUpdatedBalance(this.db.getAccountBalance(req.getCustomerId()));
						out.writeObject(res);
						break;
					case DEPOSIT:
						res.setOperation(req.getOperation());
						res.setOperationSuccess(true);
						res.setRequestedAmount(req.getAmount());
						this.db.deposit(req.getCustomerId(), req.getAmount());
						res.setUpdatedBalance(this.db.getAccountBalance(req.getCustomerId()));
						out.writeObject(res);
						break;
					case WITHDRAW:
						res.setOperation(req.getOperation());
						res.setOperationSuccess(this.db.withdraw(req.getCustomerId(), req.getAmount()));
						res.setRequestedAmount(req.getAmount());
						res.setUpdatedBalance(this.db.getAccountBalance(req.getCustomerId()));
						if (!res.isOperationSuccess())
							res.setErrorMessage(
									"You tried to withdraw more money than you currently have in your account.\nPlease try again!");
						out.writeObject(res);
						break;
					}
				}
			} catch (

			IOException ioe) {
				System.out.printf("IOException: %s%n", ioe);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		// default port number will be 8080 unless provided an input arg

		port = Constants.PORT;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);

			} catch (NumberFormatException ex) {
				System.err.printf("NumberFormatException: You have entered an invalid port number: %s%n", args[0]);
			}
		}
		launch(args);

	}

}
