Group9377_Assignment1
---

This is the assignment 1 submission for COMP303 by Group KOPS.

Group Members and their Responsiblities:
	Kevin Ma 				300867968
		Socket Programming
		Documentation
		Client Request
		Server Response

	Ostap Hamarnyk 		300836326
		JavaFX GUI

	Poulad Ashraf Pour 	300858337
		Socket Programming
		Testing

	Suthas Ganeshathasan 300838430
		Account Object
		BankDatabase Object
---

We first created Account class to represent a bank account for our ATM application.

We created a BankDatabase class to simulate persistent storage of bank accounts.

In the BankDatabase class, we used a Thread-safe collection:
	private ArrayList<AccountDTO> bankAccounts = (ArrayList<AccountDTO>) Collections.synchronizedList(new ArrayList<AccountDTO>());

	This is because one to many customers can connect to the same bank account using our online ATM client (as long as they provide the right credentials). The issue with data corruption arising when multiple clients are trying to read and write from the same ArrayList can occur here. Our choice of using a thread-safe (synchronized) collection will circumvent this problem.

We split the application into three packages for better clarity of the classes and their respective functionalities:
	atm.client
	atm.server
	atm.shared (shared resources for client and server app)

We decided to use a multi-threaded application approach for our client/server application. This is because we wanted to make the application more responsive.
	One thread is used by the application for the rendering of the GUI.
	One thread is used by the server to continuously listen for client socket connections.
	One thread is created to handle each client socket connection.

We decided to use JavaFX for our GUI in this application.
	This is because our team's GUI designer has more experience with JavaFX than Swing.

