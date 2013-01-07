

import java.io.*;

import serveur.EchoServer;
import common.*;


public class ServerConsole implements ChatIF {

	//Class variables *************************************************
	
	final public static int DEFAULT_PORT = 5555;
	
	//Instance variables **********************************************
	  
	EchoServer server;

	//Constructors ****************************************************

	public ServerConsole(int port) {
		server = new EchoServer(port, this);
	}

  
  	//Instance methods ************************************************
	  
  	public void accept() {
  		try {
  			BufferedReader fromConsole = 
  				new BufferedReader(new InputStreamReader(System.in));
  			String message;
  			
  			while (true) {
  				message = fromConsole.readLine();
  				this.server.handleMessageFromServerUI(message);
		  	}
  		}
  		catch (Exception ex) {
  			System.out.println(
  			"Unexpected error while reading from console!");
		}
	}
  	
  	public void display(String message) {
  	  	System.out.println(message);
  	}

  
  	//Class methods ***************************************************
    

	public static void main(String[] args) {
	  	int port = 0; //Port to listen on
			
	  	try {
		  	port = Integer.parseInt(args[0]); //Get port from command line
	  	}
	  	catch(Throwable t) {
		  	port = DEFAULT_PORT; //Set port to 5555
	  	}
		
	  	ServerConsole sv = new ServerConsole(port);
  		
  		System.out.println("\nStarting of the server\n");
  		
  		sv.accept();  //Wait for console data
	}
}
//End of ServerConsole class
