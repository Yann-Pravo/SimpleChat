package client;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import ocsf.client.*;
import common.*;
import java.io.*;
import java.util.*;

public class ChatClient implements Observer {
	
	//Instance variables **********************************************
 
	String id;

	public ChatIF clientUI;
	public ObservableClient clientObs;

  
	//Constructors ****************************************************
  
	public ChatClient(String id, String host, int port, ChatIF clientUI) 
		throws IOException {
		this.clientObs = new ObservableClient(host, port);
		this.id = id;
		this.clientUI = clientUI;
		this.clientObs.addObserver(this);
	}

  
	//Instance methods ************************************************

	public void handleMessageFromServer(Object msg) {
		clientUI.display(msg.toString());
	}

	public void handleMessageFromClientUI(String message) {
		try {
			if (message.equals("#quit")) {
					this.quit();
			}
			else if (message.equals("#logoff")) {
				if (this.clientObs.isConnected()) {
					this.clientObs.closeConnection();
				}
			}
			else if ((message.split(" "))[0].equals("#sethost")) {
				if (!this.clientObs.isConnected()) {
					try {
						this.clientObs.setHost((message.split(" "))[1]);
					}
					catch (ArrayIndexOutOfBoundsException e) {
						this.clientUI.display("You must choose a host to set.");
					}
				}
				else {
					this.clientUI.display(
							"You can't set an other host " +
							"when you are connected");
				}
			}
			else if ((message.split(" "))[0].equals("#setport")) {
				if (! (this.clientObs.isConnected()) ) {
					try {
						this.clientObs.setPort(
								Integer.parseInt((message.split(" "))[1]));
					}
					catch (ArrayIndexOutOfBoundsException e) {
						this.clientUI.display("You must choose a port to set.");
					}
				}
				else {
					this.clientUI.display(
							"You can't set an other port " +
							"when you are connected");
				}
			}
			else if (((message.split(" "))[0]).equals("#login")) {
				if (message.length() > 7
						&& !(message.substring(7, 8)).contains(" ")) {
					this.setId((message.split(" "))[1]);
  					
  					if (!this.clientObs.isConnected()) {
  						try {
  							this.clientObs.openConnection();
  				  			this.clientObs.sendToServer("#login " +
  				  					this.getId());
  						}
  						catch (IOException e) { 
  							this.clientUI.display(
  									"Error: Can't setup connection!"); 
  						}
  					}
  					else {
  						this.clientUI.display(
  								"\nImpossible connection, " +
  								"client already connected");
  					}
				}
				else {
					this.clientUI.display(
							"\nYou must choose a login " +
							"when using the command #login");
				}
			}
			else if (message.equals("#gethost")) {
				this.clientUI.display("Host : " + this.clientObs.getHost());
			}
			else if (message.equals("#getport")) {
				this.clientUI.display("Port : " + this.clientObs.getPort());
			}
			else {
				if (this.clientObs.isConnected()) {
					this.clientObs.sendToServer(message);
				}
			}
		}
		catch(IOException e) {
			clientUI.display(
					"Could not send message to server. Terminating client.");
			quit();
		}
	}
  
	public void quit() {
		try {
			this.clientObs.closeConnection();
		}
		catch(IOException e) {}
		System.exit(0);
	}
  
	protected void connectionClosed()
	{
		this.clientUI.display("\nserver connection terminated.\n");
	}

	protected void connectionException(Exception exception)
	{
		this.clientUI.display("\nexception : server connection interrupted\n");
	}

	protected void connectionEstablished()
	{
		this.clientUI.display("\nserver connection established on port " +
				clientObs.getPort() + ".\n");
	}
	  
	public void setId(String id) {
		this.id = id;
	}
  
	public String getId () {
		return this.id;
	}

	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			if (arg.equals(ObservableClient.CONNECTION_CLOSED)) {
				connectionClosed();
			}
			else if (arg.equals(ObservableClient.CONNECTION_ESTABLISHED)) {
				connectionEstablished();
			}
			else {
				handleMessageFromServer(arg);
			}
		}
		else if (arg instanceof Exception) {
			connectionException((Exception) arg);
		}
	}
}
//End of ChatClient class
