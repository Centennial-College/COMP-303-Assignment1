package atm.client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import atm.server.ServerResponse;
import atm.shared.Constants;

public class AtmClient extends Application {
	Stage window;
	Scene login, mainMenu, balanceView, depositView, withdrawView;
	static InetAddress hostname;
	static int port;

	TextField txtUserID, txtPin, txtDeposit, txtWithdraw;
	Button btnSignIn, btnDeposit, btnWithdraw, btnCheckBal, btnExit, btnMain, btnDepMain, btnWdMain, btnClose,
			btnDepositCash, btnWithdrawCash;
	Text errorMsg, withdrawError, depositError;
	Label lblBalance, lblAmt;

	public static void main(String[] args) {
		try {
			hostname = InetAddress.getByName(null);
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

		} catch (UnknownHostException uhe) {
			System.out.println("Server not found: " + uhe.getMessage());
		}

	}

	@Override
	public void start(Stage primaryStage) {

		// General GUI properties //

		window = primaryStage;
		primaryStage.setTitle("KOPS Bank");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text title = new Text("Welcome to KOPS Bank!\nPlease enter your ID and PIN below:");
		title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		title.setTextAlignment(TextAlignment.CENTER);
		grid.add(title, 0, 0, 2, 1);

		Label lblUserID = new Label("ID:");
		grid.add(lblUserID, 0, 2);

		txtUserID = new TextField();
		grid.add(txtUserID, 1, 2);

		Label lblPin = new Label("PIN:");
		grid.add(lblPin, 0, 3);

		txtPin = new PasswordField();
		grid.add(txtPin, 1, 3);

		btnSignIn = new Button("Sign in");
		HBox btnBox = new HBox(10);
		btnBox.setAlignment(Pos.BOTTOM_RIGHT);
		btnBox.getChildren().add(btnSignIn);
		grid.add(btnBox, 1, 4);

		errorMsg = new Text();
		errorMsg.setFill(Color.FIREBRICK);
		grid.add(errorMsg, 1, 6);

		// Account View
		GridPane menu = new GridPane();
		menu.setPadding(new Insets(20, 20, 20, 20));
		menu.setAlignment(Pos.CENTER);
		menu.setVgap(10);

		Text welcomeMsg = new Text("What would you like to do today?");
		welcomeMsg.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));

		btnDeposit = new Button("Deposit");
		btnWithdraw = new Button("Withdraw");
		btnCheckBal = new Button("Check Balance");
		btnExit = new Button("End Session");

		btnDeposit.setMinWidth(275);
		btnWithdraw.setMinWidth(275);
		btnCheckBal.setMinWidth(275);
		btnExit.setMinWidth(275);

		btnDeposit.setMinHeight(50);
		btnWithdraw.setMinHeight(50);
		btnCheckBal.setMinHeight(50);
		btnExit.setMinHeight(50);

		menu.add(welcomeMsg, 0, 0, 2, 1);
		menu.add(btnCheckBal, 0, 1);
		menu.add(btnDeposit, 0, 2);
		menu.add(btnWithdraw, 0, 3);
		menu.add(btnExit, 0, 4);

		// Balance View
		GridPane balPane = new GridPane();

		balPane.setPadding(new Insets(0, 10, 10, 10));
		balPane.setAlignment(Pos.CENTER);
		balPane.setVgap(15);

		lblBalance = new Label("Your updated balance is $244.25");
		lblBalance.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		lblBalance.setWrapText(true);
		lblBalance.setTextAlignment(TextAlignment.CENTER);
		lblAmt = new Label("You have successfully deposited $25");
		lblAmt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		lblAmt.setWrapText(true);
		btnMain = new Button("Main Menu");
		btnClose = new Button("Exit");

		balPane.add(lblAmt, 0, 0, 2, 1);
		balPane.add(lblBalance, 0, 2, 2, 1);
		balPane.add(btnClose, 3, 4);
		balPane.add(btnMain, 0, 4);

		// Deposit View
		GridPane depPane = new GridPane();
		depPane.setAlignment(Pos.CENTER);
		depPane.setVgap(15);
		depPane.setHgap(15);

		btnDepMain = new Button("Main Menu");
		Label lblDeposit = new Label("Please enter amount you would like to deposit:");
		lblDeposit.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
		lblDeposit.setWrapText(true);
		btnDepositCash = new Button("Deposit");
		btnDepositCash.setMinSize(100, 40);
		txtDeposit = new TextField();
		txtDeposit.setMinSize(250, 40);
		depositError = new Text();
		depositError.setFill(Color.FIREBRICK);
		depositError.setWrappingWidth(300);

		depPane.add(lblDeposit, 0, 0, 2, 1);
		depPane.add(txtDeposit, 0, 2);
		depPane.add(btnDepositCash, 1, 2);
		depPane.add(depositError, 0, 4, 2, 1);

		BorderPane depBorderPane = new BorderPane();
		depBorderPane.setPadding(new Insets(15, 15, 15, 15));
		depBorderPane.setCenter(depPane);
		depBorderPane.setBottom(btnDepMain);

		// Withdraw View
		GridPane wdPane = new GridPane();
		wdPane.setAlignment(Pos.CENTER);
		wdPane.setVgap(15);
		wdPane.setHgap(15);

		btnWdMain = new Button("Main Menu");
		Label lblWithdraw = new Label("Please enter amount you would like to withdraw:");
		lblWithdraw.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
		lblWithdraw.setWrapText(true);
		btnWithdrawCash = new Button("Withdraw");
		btnWithdrawCash.setMinSize(100, 40);
		txtWithdraw = new TextField();
		txtWithdraw.setMinSize(250, 40);
		withdrawError = new Text();
		withdrawError.setFill(Color.FIREBRICK);
		withdrawError.setWrappingWidth(300);

		wdPane.add(lblWithdraw, 0, 0, 2, 1);
		wdPane.add(txtWithdraw, 0, 2);
		wdPane.add(btnWithdrawCash, 1, 2);
		wdPane.add(withdrawError, 0, 4, 2, 1);

		BorderPane wdBorderPane = new BorderPane();
		wdBorderPane.setCenter(wdPane);
		wdBorderPane.setBottom(btnWdMain);
		wdBorderPane.setPadding(new Insets(0, 0, 15, 15));

		withdrawView = new Scene(wdBorderPane, 400, 450);
		depositView = new Scene(depBorderPane, 400, 450);
		balanceView = new Scene(balPane, 400, 450);
		mainMenu = new Scene(menu, 400, 450);
		login = new Scene(grid, 400, 450);
		window.setScene(login);
		primaryStage.setResizable(false);
		primaryStage.show();

		Socket socket = null;
		try {
			socket = new Socket(hostname, port);

			new Thread(new ProcessingThread(socket)).start();

		} catch (UnknownHostException ex) {
			System.out.println("Server not found: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("I/O Error: " + ex.getMessage());
		}
	}

	private class ProcessingThread implements Runnable {
		private Socket socket;

		private ObjectOutputStream out;
		private ObjectInputStream in;
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
		}

		@Override
		public void run() {
			try {
				// the thread should be doing either one of two things:
				// 1. soliciting input from user
				goToScreen(currentScreen);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*
		 * For Console App, will not need this with GUI
		 * 
		 * -1 = awaiting user input
		 * 
		 * 0 = Main Menu
		 */
		private void goToScreen(Screen screen) throws IOException {
			// NOTE: only let execution leave this loop if server expected to send
			// response/exiting atm client...otherwise error occurs.

			// ^May not have this issue when moving over to GUI since we will be using
			// Event-driven handlers instead of continuous loop reading from
			// objectinputstream

			// didn't do input validation yet - will do with GUI

			switch (screen) {

			case LOGIN:
				txtUserID.textProperty().addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						if (!newValue.matches("\\d{0,7}?")) {
							txtUserID.setText(oldValue);
						}
					}
				});
				txtPin.textProperty().addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						if (!newValue.matches("\\d{0,4}?")) {
							txtPin.setText(oldValue);
						}
					}
				});
				btnSignIn.setOnAction(e -> {
					if (!(txtPin.getText().equals("") || txtUserID.getText().equals(""))) {
						custId = Integer.parseInt(txtUserID.getText());
						pin = Integer.parseInt(txtPin.getText());
						req = ClientRequest.authenticate(custId, pin);
						try {
							out.writeObject(req);
							processServerRes();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else {
						errorMsg.setText("Please enter both ID and PIN!");
					}
				});
				break;
			case MAIN_MENU:
				window.setScene(mainMenu);

				btnCheckBal.setOnAction(e -> {
					try {
						currentScreen = Screen.BALANCE_INQUIRY;
						goToScreen(currentScreen);
						req = ClientRequest.balanceInquiry(custId, pin);
						out.writeObject(req);
						processServerRes();
						lblBalance.setText(String.format("Your current balance is $%.2f", res.getUpdatedBalance()));
						lblAmt.setText("");
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});
				btnDeposit.setOnAction(e -> {
					try {
						currentScreen = Screen.DEPOSIT_PROMPT_AMOUNT;
						goToScreen(currentScreen);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});
				btnWithdraw.setOnAction(e -> {
					try {
						currentScreen = Screen.WITHDRAWAL_PROMPT_AMOUNT;
						goToScreen(currentScreen);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});
				btnExit.setOnAction(e -> {
					window.close();
				});
				break;
			case BALANCE_INQUIRY:
			case DEPOSIT_RESULTS:
			case WIDTHDRAWAL_RESULTS:
				window.setScene(balanceView);
				btnMain.setOnAction(e -> {
					try {
						currentScreen = Screen.MAIN_MENU;
						goToScreen(currentScreen);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});
				btnClose.setOnAction(e -> {
					window.close();
				});
				break;
			case DEPOSIT_PROMPT_AMOUNT:
				window.setScene(depositView);
				txtDeposit.setText("");
				depositError.setText("");
				txtDeposit.textProperty().addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
							txtDeposit.setText(oldValue);
						}
					}
				});
				btnDepositCash.setOnAction(e -> {
					try {
						if (!txtDeposit.getText().equals("")) {
							amt = Double.parseDouble(txtDeposit.getText());
							req = ClientRequest.deposit(custId, pin, amt);
							out.writeObject(req);
							processServerRes();
							lblBalance.setText(String.format("Your updated balance is $%.2f", res.getUpdatedBalance()));
							lblAmt.setText(String.format("You have successfully deposited $%.2f", amt));
							currentScreen = Screen.DEPOSIT_RESULTS;
							goToScreen(currentScreen);
						} else {
							currentScreen = Screen.DEPOSIT_PROMPT_AMOUNT;
							depositError.setText("Please specify the amount you would like to deposit!");
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});
				btnDepMain.setOnAction(e -> {
					try {
						currentScreen = screen.MAIN_MENU;
						goToScreen(currentScreen);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
				break;
			case WITHDRAWAL_PROMPT_AMOUNT:
				window.setScene(withdrawView);
				txtWithdraw.setText("");
				txtWithdraw.textProperty().addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
							txtWithdraw.setText(oldValue);
						}
					}
				});
				btnWithdrawCash.setOnAction(e -> {
					try {
						if (!txtWithdraw.getText().equals("")) {
							// withdrawError.setText("");
							amt = Double.parseDouble(txtWithdraw.getText());
							req = ClientRequest.withdraw(custId, pin, amt);
							out.writeObject(req);
							processServerRes();
							lblBalance.setText(String.format("Your updated balance is $%.2f", res.getUpdatedBalance()));
							lblAmt.setText(String.format("You have successfully withdrawn $%.2f", amt));
						} else {
							withdrawError.setText("Please specify the amount you want to withdraw!");
							currentScreen = screen.WITHDRAWAL_PROMPT_AMOUNT;
						}
						goToScreen(currentScreen);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});
				btnWdMain.setOnAction(e -> {
					try {
						withdrawError.setText("");
						currentScreen = screen.MAIN_MENU;
						goToScreen(currentScreen);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
				break;
			}
		}

		private void processServerRes() {
			try {
				if ((res = (ServerResponse) in.readObject()) != null) {
					// for fail-proof operations, simply display their results
					// some operations can fail, would need handling
					switch (req.getOperation()) {
					case AUTHENTICATE:
						if (!res.isOperationSuccess()) {
							currentScreen = Screen.LOGIN;
							errorMsg.setText(res.getErrorMessage());
						} else {
							currentScreen = Screen.MAIN_MENU;
							goToScreen(currentScreen);
						}
						break;
					case WITHDRAW:
						withdrawError.setText("");
						if (!res.isOperationSuccess()) {
							currentScreen = Screen.WITHDRAWAL_PROMPT_AMOUNT;
							withdrawError.setText(res.getErrorMessage());
						} else {
							currentScreen = Screen.WIDTHDRAWAL_RESULTS;
							goToScreen(currentScreen);
						}
						break;
					}
				}
			} catch (Exception ex) {

			}
		}
	}
}
