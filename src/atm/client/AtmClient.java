package atm.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import atm.server.ServerResponse;
import atm.shared.Constants;

public class AtmClient {
	private InetAddress hostname;
	private int port;

	public AtmClient(InetAddress hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public void start() {
		Socket socket = null;
		try {
			socket = new Socket(hostname, port);

			System.out.printf("ATM Client connected to the Bank Server: %s on port: %s%n", hostname, port);

			// (new Thread(new ReadThread(socket))).start();
			// (new Thread(new WriteThread(socket))).start();
			new Thread(new ProcessingThread(socket)).start();

		} catch (UnknownHostException ex) {
			System.out.println("Server not found: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("I/O Error: " + ex.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			InetAddress hostname = InetAddress.getByName(null);

			// default port number will be 8080 unless provided an input arg
			int port = Constants.PORT;
			if (args.length > 0) {
				try {
					port = Integer.parseInt(args[0]);
				} catch (NumberFormatException ex) {
					System.err.printf("NumberFormatException: You have entered an invalid port number: %s%n", args[0]);
				}
			}

			AtmClient atm = new AtmClient(hostname, port);
			atm.start();

		} catch (UnknownHostException uhe) {
			System.out.println("Server not found: " + uhe.getMessage());
		}

	}

	private class ProcessingThread implements Runnable {
		private Socket socket;

		private ObjectOutputStream out;
		private ObjectInputStream in;
		// PrintWriter writer;
		private Scanner sc = new Scanner(System.in);
		// ---
		private ClientRequest req;
		private ServerResponse res;
		// ---
		private Screen currentScreen = Screen.LOGIN;
		private int userInput = -1;
		private boolean exitAtmClient = false;
		// ---
		private int custId;
		private int pin;
		private double amt;

		public ProcessingThread(Socket socket) throws IOException {
			this.socket = socket;
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			// this.writer = new PrintWriter(new
			// OutputStreamWriter(socket.getOutputStream()), true);
		}

		@Override
		public void run() {
			try {
				// do {
				while (true) {
					// writer.flush();
					// the thread should be doing either one of two things:
					// 1. soliciting input from user
					// prevents the screen from displaying repeated screens
					if (currentScreen != null) {
						displayScreen(currentScreen);
					}

					if (exitAtmClient)
						// if (userInput.equalsIgnoreCase("exit"))
						break;

					// 2. displaying response from server to user
					if ((res = (ServerResponse) in.readObject()) != null) {
						System.out.println("\nSERVER >>");
						System.out.println(res + "\n");

						switch (req.getOperation()) {
						case AUTHENTICATE:
							if (!res.isOperationSuccess()) {
								currentScreen = Screen.LOGIN;
								continue;
							}
							currentScreen = Screen.MAIN_MENU;
							break;
						case BALANCE_INQUIRY:
							System.out.printf("Your balance is currently: $%.2f", res.getUpdatedBalance());
							break;
						case DEPOSIT:
							break;
						case WITHDRAW:
							break;
						}
						// if invalid authorization, prompt them to do it again
						// if (req.getOperation() == 'a' && !res.isOperationSuccess()) {
						// currentScreen = 0;
						// continue;
						// }
						//
						// if (req.getOperation() == 'a' && res.isOperationSuccess()) {
						// currentScreen = 0;
						// continue;
						// }
					}

					// System.out.print("getting normal user input: ");
					// userInput = sc.nextLine();
					// writer.println(userInput);

					// this.socket.close();
					// get a SocketException typeof IOException when closing socket and another
					// thread tries to use it still...so need to
					// we won't have this problem in GUI because only using on thread for i/o there
					// not two diff threads
					// isConnected() and isClosed() do not help here.
				}
				System.out.println("outside main while loop");
				// while (!userInput.equalsIgnoreCase("exit"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		/*
		 * For Console App, will not need this with GUI
		 * 
		 * -1 = awaiting user input
		 * 
		 * 0 = Main Menu
		 */
		private void displayScreen(Screen screen) throws IOException {
			// reset to null
			currentScreen = null;

			// didn't do input validation yet - will do with GUI
			switch (screen) {
			case LOGIN:
				System.out.println("\nWelcome to KOPS Bank!\n");
				System.out.println("Please login below: ");
				System.out.print("ID: ");
				custId = sc.nextInt();
				System.out.print("PIN: ");
				pin = sc.nextInt();

				req = ClientRequest.authenticate(custId, pin);

				// tell server to receive an object
				// writer.println("objMsg");
				out.writeObject(req);

				break;
			case MAIN_MENU:
				System.out.printf("Account #: %s%n---%n", this.custId);
				System.out.println("Please enter your selected option from below:");
				System.out.println("[1]. Check your balance.");
				System.out.println("[2]. Deposit money from your account.");
				System.out.println("[3]. Withdraw money from your account.");
				System.out.println("[4]. Exit ATM.");
				userInput = sc.nextInt();
				switch (userInput) {
				case 1:
					currentScreen = Screen.BALANCE_INQUIRY;
					break;
				case 2:
					currentScreen = Screen.DEPOSIT_PROMPT_AMOUNT;
					break;
				case 3:
					currentScreen = Screen.WITHDRAWAL_PROMPT_AMOUNT;
					break;
				case 4:
					exitAtmClient = true;
					System.out.println("\nThank you for doing business with KOPS!\nHave a great day!\n");
					break;
				}
				break;
			case BALANCE_INQUIRY:
				System.out.printf("Account #: %s%nBALANCE INQUIRY%n---%n", this.custId);
				req = ClientRequest.balanceInquiry(custId, pin);
				// writer.println("objMsg");
				out.writeObject(req);
				break;
			case DEPOSIT_PROMPT_AMOUNT:
				break;
			case WITHDRAWAL_PROMPT_AMOUNT:
				break;
			case DEPOSIT_RESULTS:
				break;
			case WIDTHDRAWAL_RESULTS:
				break;
			}
		}
	}

}
