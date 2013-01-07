

// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Ivan VALIMAHAMED
 * @author Mathieu VIBERT
 * @version December 2009
 */
public class ClientConsole implements ChatIF  {

	//Class variables *************************************************
  
	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;
  
	//Instance variables **********************************************
  
	/**
	 * The instance of the client that created this ConsoleChat.
	 */
	ChatClient client;

  
	//Constructors ****************************************************

	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 * @param id the id of the client
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 */
	public ClientConsole(String id, String host, int port) {
		try {
			client = new ChatClient(id, host, port, this);
		} 
		catch(IOException exception) {
			System.out.println("Error: Can't setup connection ! " +
					"Terminating client.");
			System.exit(1);
		}
	}

  
  	//Instance methods ************************************************
  
  	/**
  	 * This method waits for input from the console.  Once it is 
  	 * received, it sends it to the client's message handler.
  	 */
  	public void accept() {
  		try {
  			BufferedReader fromConsole = 
  				new BufferedReader(new InputStreamReader(System.in));
  			String message;
  			
  			while (true) {
  				message = fromConsole.readLine();
  				this.client.handleMessageFromClientUI(message);
  			}
  		} 
  		catch (Exception ex) {
  			System.out.println("Unexpected error");
  		}
  	}

  	/**
  	 * This method overrides the method in the ChatIF interface.  It
  	 * displays a message onto the screen.
  	 *
  	 * @param message The string to be displayed.
  	 */
  	public void display(String message) {
  	  	System.out.println(message);
  	}

  
  	//Class methods ***************************************************
  
  	/**
  	 * This method is responsible for the creation of the Client UI.
  	 *
  	 * @param args[0] The id to connect with.
  	 * @param args[1] The port to connect on.
  	 * @param args[2] The host to connect to.
  	 */
  	public static void main(String[] args) {
  		String host = "";
  		String id = "";
  		int port = 0;  //The port number
  		
  		try {
  			id = args[0];
  		}
  		catch(ArrayIndexOutOfBoundsException e) {
  			id = "ANONYMOUS";
  		}
  		  		
  		try {
  			host = args[2];
  		}
  		catch(ArrayIndexOutOfBoundsException e) {
  			host = "localhost";
  		}
  		catch(NumberFormatException e) {
  			host = "localhost";
  		}
  		
  		try {
  			port = Integer.parseInt(args[1]);
  		}
  		catch(ArrayIndexOutOfBoundsException e) {
  			port = DEFAULT_PORT;
  		}
  		catch(NumberFormatException e) {
  			port = DEFAULT_PORT;
  		}
    
  		ClientConsole chat = new ClientConsole(id, host, port);
  		
  		
  		System.out.println("\nStarting of client :" + chat.client.getId() + "\n");
  		
  		chat.accept();  //Wait for console data
  	}
}
//End of ConsoleChat class
