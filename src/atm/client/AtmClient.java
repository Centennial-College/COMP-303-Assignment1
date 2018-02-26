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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 *
 * The main application to be run on the client side. This class contains the
 * GUI for the Atm client application.
 *
 */
public class AtmClient extends Application {
	Stage window;
	Scene loginView, mainMenuView, balanceView, depositView, withdrawView;
	static InetAddress hostname;
	static int port;

	TextField txtUserID, txtPin, txtDeposit, txtWithdraw;
	Button btnSignIn, btnDeposit, btnWithdraw, btnCheckBal, btnMainMenuExit, btnMain, btnDepMain, btnWdMain,
			btnBalInqClose, btnDepositCash, btnWithdrawCash, btnLoginExit;
	Text errorMsg, withdrawError, depositError;
	Label lblBalance, lblAmt;

	public static void main(String[] args) {
		try {
			hostname = InetAddress.getByName(null);
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
		primaryStage.setTitle("KOPS Bank: ATM Client");

		// Login View
		GridPane loginPane = new GridPane();
		loginPane.setAlignment(Pos.CENTER);
		loginPane.setHgap(10);
		loginPane.setVgap(10);
		loginPane.setPadding(new Insets(25, 25, 25, 25));

		Text title = new Text("Welcome to KOPS Bank!\nPlease enter your ID and PIN below:");
		title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		title.setTextAlignment(TextAlignment.CENTER);
		loginPane.add(title, 0, 0, 2, 1);

		Label lblUserID = new Label("ID:");
		loginPane.add(lblUserID, 0, 2);

		txtUserID = new TextField();
		loginPane.add(txtUserID, 1, 2);

		Label lblPin = new Label("PIN:");
		loginPane.add(lblPin, 0, 3);

		txtPin = new PasswordField();
		loginPane.add(txtPin, 1, 3);

		btnSignIn = new Button("Sign in");
		HBox btnBox = new HBox(10);
		btnBox.setAlignment(Pos.BOTTOM_RIGHT);
		btnBox.getChildren().add(btnSignIn);
		btnLoginExit = new Button("Exit");
		btnBox.getChildren().add(btnLoginExit);
		loginPane.add(btnBox, 1, 4);

		errorMsg = new Text();
		errorMsg.setFill(Color.FIREBRICK);
		loginPane.add(errorMsg, 1, 6);

		// Main Menu View
		GridPane menuPane = new GridPane();
		menuPane.setPadding(new Insets(20, 20, 20, 20));
		menuPane.setAlignment(Pos.CENTER);
		menuPane.setVgap(10);

		Text welcomeMsg = new Text("What would you like to do today?");
		welcomeMsg.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));

		btnDeposit = new Button("Deposit");
		btnWithdraw = new Button("Withdraw");
		btnCheckBal = new Button("Check Balance");
		btnMainMenuExit = new Button("End Session");

		btnDeposit.setMinWidth(275);
		btnWithdraw.setMinWidth(275);
		btnCheckBal.setMinWidth(275);
		btnMainMenuExit.setMinWidth(275);

		btnDeposit.setMinHeight(50);
		btnWithdraw.setMinHeight(50);
		btnCheckBal.setMinHeight(50);
		btnMainMenuExit.setMinHeight(50);

		menuPane.add(welcomeMsg, 0, 0, 2, 1);
		menuPane.add(btnCheckBal, 0, 1);
		menuPane.add(btnDeposit, 0, 2);
		menuPane.add(btnWithdraw, 0, 3);
		menuPane.add(btnMainMenuExit, 0, 4);

		// Balance View
		GridPane balancePane = new GridPane();

		balancePane.setPadding(new Insets(0, 10, 10, 10));
		balancePane.setAlignment(Pos.CENTER);
		balancePane.setVgap(15);

		lblBalance = new Label("Your updated balance is $244.25");
		lblBalance.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		lblBalance.setWrapText(true);
		lblBalance.setTextAlignment(TextAlignment.CENTER);
		lblAmt = new Label("You have successfully deposited $25");
		lblAmt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		lblAmt.setWrapText(true);
		btnMain = new Button("Main Menu");
		btnBalInqClose = new Button("Exit");

		balancePane.add(lblAmt, 0, 0, 2, 1);
		balancePane.add(lblBalance, 0, 2, 2, 1);
		balancePane.add(btnBalInqClose, 3, 4);
		balancePane.add(btnMain, 0, 4);

		// Deposit View
		GridPane depositPane = new GridPane();
		depositPane.setAlignment(Pos.CENTER);
		depositPane.setVgap(15);
		depositPane.setHgap(15);

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

		depositPane.add(lblDeposit, 0, 0, 2, 1);
		depositPane.add(txtDeposit, 0, 2);
		depositPane.add(btnDepositCash, 1, 2);
		depositPane.add(depositError, 0, 4, 2, 1);

		BorderPane depBorderPane = new BorderPane();
		depBorderPane.setPadding(new Insets(15, 15, 15, 15));
		depBorderPane.setCenter(depositPane);
		depBorderPane.setBottom(btnDepMain);

		// Withdraw View
		GridPane withdrawPane = new GridPane();
		withdrawPane.setAlignment(Pos.CENTER);
		withdrawPane.setVgap(15);
		withdrawPane.setHgap(15);

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

		withdrawPane.add(lblWithdraw, 0, 0, 2, 1);
		withdrawPane.add(txtWithdraw, 0, 2);
		withdrawPane.add(btnWithdrawCash, 1, 2);
		withdrawPane.add(withdrawError, 0, 4, 2, 1);

		BorderPane wdBorderPane = new BorderPane();
		wdBorderPane.setCenter(withdrawPane);
		wdBorderPane.setBottom(btnWdMain);
		wdBorderPane.setPadding(new Insets(0, 0, 15, 15));

		withdrawView = new Scene(wdBorderPane, 400, 450);
		depositView = new Scene(depBorderPane, 400, 450);
		balanceView = new Scene(balancePane, 400, 450);
		mainMenuView = new Scene(menuPane, 400, 450);
		loginView = new Scene(loginPane, 400, 450);
		window.setScene(loginView);
		primaryStage.setResizable(false);
		primaryStage.show();

		// closes client when closing GUI
		primaryStage.setOnCloseRequest(e -> {
			// prevent user from closing app by pressing x button
			e.consume();

			// show instructions dialog
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("How to Disconnect");
			alert.setHeaderText("Proper way to disconnect:");
			alert.setContentText(
					"Please use the Exit and Close buttons found on either:\n1. Main Menu Screen\n2. Balance Inquiry Screen, or\n3. Login Screen");
			alert.show();
		});

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
				goToScreen(currentScreen);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void goToScreen(Screen screen) throws IOException {
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
				btnLoginExit.setOnAction(e -> {
					req = ClientRequest.exitSession(-1, pin);
					try {
						out.writeObject(req);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Platform.exit();
				});
				break;
			case MAIN_MENU:
				window.setScene(mainMenuView);

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
				btnMainMenuExit.setOnAction(e -> {
					req = ClientRequest.exitSession(custId, pin);
					try {
						out.writeObject(req);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Platform.exit();
				});
				break;

			// all have same behavior
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
				btnBalInqClose.setOnAction(e -> {
					req = ClientRequest.exitSession(custId, pin);
					try {
						out.writeObject(req);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Platform.exit();
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
