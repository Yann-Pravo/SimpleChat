package serveur;


// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;

import ocsf.server.*;

import java.util.*;
import common.*;


/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */

public class EchoServer implements Observer {

	//Class variables *************************************************
	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;
	
	public ChatIF serverUI;
	public ObservableOriginatorServer serverObs;

	//Constructors ****************************************************
  
	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 * @param serverUI The interface type variable.
	 */
	public EchoServer(int port, ChatIF serverUI) {
		this.serverUI = serverUI;
		this.serverObs = new ObservableOriginatorServer(port);
		this.serverObs.addObserver(this);
	}

  
	//Instance methods ************************************************
  
	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	public void handleMessageFromClient
		(Object msg, ConnectionToClient client) {
		Object info;
		
		if (client.getInfo("String") == null
				&& (!msg.toString().contains("#login"))) {
			try {
				client.sendToClient("Please connect yourself!");
				client.close();
			} catch (IOException e) {
				this.serverUI.display("Impossible disconnection");
			}
		}
		else if (client.getInfo("String") != null
				&& msg.toString().contains("#login")) {
			try {
				client.sendToClient("You are already connected!");
			} catch (IOException e) {}
		}
		else if (client.getInfo("String") == null
				&& msg.toString().contains("#login")) {
			try {
				info = msg.toString().substring(7);
			}
			catch (ArrayIndexOutOfBoundsException e){
				info = client.getId() + "";
			}

			client.setInfo("String", info);
			
			clientConnected(client);
		}
		else {
			this.serverUI.display("Client " + client.getInfo("String") +
					" MSG > " + msg);

			this.serverObs.sendToAllClients("client " +
					client.getInfo("String") + " > " + msg);
		}
	}

	/**
	 * This method handles all data coming from the UI            
	 * The action to perform depends on the mesage typed in, 
	 * specially when the message starts with the "#" character
	 * @param message The message from the UI.    
	 */
	public void handleMessageFromServerUI(String message) {
		
		if (message.equals("#quit")) {
			this.quit();
		}
		else if (message.equals("#close")) {
			try {
					this.serverObs.close();
			} catch (IOException e) {
				this.serverUI.display("Input error on the server side");
			}
		}
		else if ((message.split(" "))[0].equals("#setport")) {
			if (!this.serverObs.isListening()) {
				try {
					this.serverObs.setPort(
							Integer.parseInt((message.split(" "))[1]));
				}
				catch (ArrayIndexOutOfBoundsException e) {
					this.serverUI.display("You must choose a port to set.");
				}
			}
			else {
				this.serverUI.display(
						"Can't change the port, the server is still listening.");
			}
		}
		else if (message.equals("#start")) {
			if (!this.serverObs.isListening()) {
				try {
					this.serverObs.listen();
				}
				catch (IOException e) { 
					this.serverUI.display(
							"Error: Can't start listening connections!"); 
				}
			}
			else {
				this.serverUI.display(
						"Impossible connection, server already connected");
			}
		}
		else if ((message.split(" "))[0].equals("#getport")) {
			this.serverUI.display("Port : " + this.serverObs.getPort());
		}
		else {
			this.serverObs.sendToAllClients("Server MSG > " + message);
		}
	}

	
	/**
	 * This method terminates the server.
	 */
	public void quit() {
		try {
			this.serverObs.close();
		}
		catch(IOException e) {}
		System.exit(0);
	}
  
	/**
	 * This method overrides the one in the superclass.  Called
	 * when a client is connected.
	 * 
	 * @param client the connection with the client.
	 */
	protected synchronized void clientConnected(ConnectionToClient client) {
		if (client.getInfo("String") == null) {
			this.serverUI.display("\nnew connection of a client with the id " + 
					client.getId() + " and with the address " +
					client.getInetAddress() + "\n");
		}
		else {
			this.serverUI.display("\nconnection of the client " + 
					client.getInfo("String") + "\n");

			this.serverObs.sendToAllClients("\nconnection of the client " + 
					client.getInfo("String") + "\n");
		}
	}

	/**
	 * This method overrides the one in the superclass.  Called
	 * when a client is disconnected.
	 * 
	 * @param client the connection with the client.
	 */
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		this.serverUI.display("\nclient " + client.getInfo("String") +
				" disconnection.\n");

		this.serverObs.sendToAllClients("\ndisconnection of the client " +
				client.getInfo("String") + "\n");
	}

	/**
	 * This method overrides the one in the superclass.  Called 
	 * each time an exception is thrown in a ConnectionToClient thread.
	 *
	 * @param client the client that raised the exception.
	 */
	synchronized protected void clientException(
			ConnectionToClient client) {
		this.serverUI.display("\nexception : client " +
				client.getInfo("String") +
				" connection interrupted.\n");
	}

	/**
	 * This method overrides the one in the superclass.  Called
	 * when the server stops accepting connections 
	 * because an exception has been raised.
	 */
	protected void listeningException() {
		this.serverUI.display(
		"\nexception : Server connections listening interrupted.\n");
	}

	/**
	 * This method overrides the one in the superclass.  Called
	 * when the server starts listening for connections.
	 */
	protected void serverStarted()
	{
		this.serverUI.display(
				"\nServer listening for connections on port " +
				this.serverObs.getPort() + "\n");
	}
  
	/**
	 * This method overrides the one in the superclass.  Called
	 * when the server stops listening for connections.
	 */
	public void serverStopped()
	{
		this.serverUI.display(
				"\nServer has stopped listening for connections.\n");
	}
  
	/**
	 * This method overrides the one in the superclass.  Called
	 * when the server is closed.
	 */
	protected void serverClosed() {
		this.serverUI.display("\nServer has been closed.\n");
	}

	/**
	 * Method used to implement Observer class
	 * @param o
	 * @param arg
	 */
	public void update(Observable o, Object arg) {
		String message;
		ConnectionToClient originator;

		originator = ((OriginatorMessage) arg).getOriginator();
		message = ((String) (((OriginatorMessage) arg).getMessage()));
		
		if (originator == null) {
			if ( message.equals(
					ObservableOriginatorServer.SERVER_STARTED) ) {
				serverStarted();
			}
			else if ( message.equals(
					ObservableOriginatorServer.SERVER_STOPPED) ) {
				serverStopped();
			}
			else if ( message.equals(
					ObservableOriginatorServer.SERVER_CLOSED) ) {
				serverClosed();
			}
			else if ( message.equals(
					ObservableOriginatorServer.LISTENING_EXCEPTION) ) {
				listeningException();
			}
		}
		else {
			if ( message.equals(
					ObservableOriginatorServer.CLIENT_DISCONNECTED) ) {
				clientDisconnected(originator);
			}
			else if ( message.equals(
					ObservableOriginatorServer.CLIENT_CONNECTED) ) {
				clientConnected(originator);
			}
			else if ( message.contains(
					ObservableOriginatorServer.CLIENT_EXCEPTION) ) {
				clientException(originator);
			}
			else {
				handleMessageFromClient(((OriginatorMessage) arg).getMessage(),
						(ConnectionToClient)
						(((OriginatorMessage) arg).getOriginator()));
			}
		}
	}
}
//End of EchoServer class
