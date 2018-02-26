package atm.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import atm.client.ClientRequest;
import atm.shared.Constants;
import atm.shared.Operations;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * The main application to be run on the server side. This class provides a
 * basic GUI to display server 'events' and logs it in a console fashion.
 *
 */
public class BankServer extends Application {
	private static int port;
	private BankDatabase db = new BankDatabase();
	Stage window;
	TextArea messages = new TextArea();

	@Override
	public void start(Stage primaryStage) {

		window = primaryStage;
		messages.setPrefHeight(550);
		messages.setEditable(false);

		VBox box = new VBox(10, messages);
		box.setPrefSize(500, 600);

		// closes server when closing GUI
		primaryStage.setOnCloseRequest((WindowEvent) -> {
			System.exit(0);
		});

		Scene main = new Scene(box);
		window.setScene(main);
		primaryStage.setTitle("KOPS Bank: Server");
		primaryStage.show();

		// javaFX runs on a thread to display GUI
		// if we try to do blocking events on that thread i.e. I/O, it will block the
		// GUI (cause the app to become non-responsive)
		new Thread(() -> {
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				messages.appendText(String.format("Server is listening on port: %s%n", port));

				// continuously check for new atm client connections
				while (true) {
					Socket socket = serverSocket.accept();
					new Thread(new ConnectionHandler(socket, this.db)).start();
					messages.appendText(String.format("New ATM Client connected.%n"));
				}
			} catch (IOException ioe) {
				System.err.printf("IOException: %s%n", ioe);
				ioe.printStackTrace();
			}
		}).start();
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

					if (req.getOperation() != Operations.EXIT) {
						messages.appendText(String.format("%nCLIENT REQUEST >>%n%s", req));

						if (res.isOperationSuccess()) {
							messages.appendText(String.format(" => SUCCESS%n"));
						} else {
							messages.appendText(String.format(" => FAILED%n"));
						}
					} else {
						// if no user logged in, custId will be -1
						if (req.getCustomerId() == -1) {
							messages.appendText(
									String.format("%nATM Client disconnected.%n---%n", req.getCustomerId()));
						} else {
							messages.appendText(String.format("%nATM Client - Account #: %s disconnected.%n---%n",
									req.getCustomerId()));
						}
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
