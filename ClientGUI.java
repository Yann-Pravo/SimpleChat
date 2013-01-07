

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import client.*;
import common.*;

public class ClientGUI extends JFrame implements MouseListener, ActionListener,
		KeyListener, ChatIF {

	private static final long serialVersionUID = 1L;


	JPanel panFenetre;	
	JPanel panLogin;
	JPanel panAff;
	JPanel panSaisie;
	
	BorderLayout borderLayout;
	FlowLayout flowLayout;
	Dimension tailleFenetre;
	
	JLabel askLogin;
	JTextField saisieLogin;
	JButton confirmLogin;
	JButton envoiMsg;
		
	JTextPane zoneSaisie;
	JTextPane zoneAffichage;
	
	JScrollPane sc;
	

	Color backgroundColor = new Color(27,109,182);
	
	final public static int DEFAULT_PORT = 5555;
	ChatClient client;
	
	public ClientGUI(String id, String host, int port) {
		
		Dimension dimensions = (Toolkit.getDefaultToolkit()).getScreenSize();
		int hauteurEcran = dimensions.height;
		int largeurEcran = dimensions.width;

		tailleFenetre = new Dimension (400, 360);
		
		this.setSize(tailleFenetre);
		this.setResizable(false);
		this.setLocation(largeurEcran / 3, hauteurEcran / 4);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		borderLayout = new BorderLayout();
		flowLayout = new FlowLayout();
		
		fillLoginFrame();
		fillChatFrame();
		
		this.setVisible(true);
		
		try {
			client = new ChatClient(id, host, port, this);
		} 
		catch(IOException exception) {
			this.display("Error: Can't setup connection!");
			System.exit(1);
		}
	}
	
	public void fillLoginFrame() {
		int inputHeight = 20;
		int verticalPosition = tailleFenetre.width / 2 - 80;
		
		this.setTitle("Client");
		panFenetre = new JPanel();
		panFenetre.setBackground(backgroundColor);
		panFenetre.setLayout(borderLayout);
		
		askLogin = new JLabel("Welcome ! Please tape your login");
		saisieLogin = new JTextField("");
		confirmLogin = new JButton("Connect");

		saisieLogin.addKeyListener(this);
		saisieLogin.addMouseListener(this);
		confirmLogin.addKeyListener(this);
		confirmLogin.addActionListener(this);
		
		askLogin.setBounds(verticalPosition, 0, 300, 20);
		saisieLogin.setBounds(verticalPosition, askLogin.getHeight() + 10, 150,
				inputHeight);
		confirmLogin.setBounds(verticalPosition,
				askLogin.getHeight() + saisieLogin.getHeight() + 20, 150,
				inputHeight);
		
		panLogin = new JPanel();
		panLogin.setBackground(backgroundColor);
		panLogin.setLayout(null);
		panLogin.add(askLogin);
		panLogin.add(saisieLogin); 
		panLogin.add(confirmLogin);
		
		panFenetre.add(panLogin,BorderLayout.CENTER);

		this.add(panFenetre);
	}
	
	public void fillChatFrame() {
		int outputHeight = 240;
		int inputHeight = 60;
		
		
		panAff = new JPanel();
		panAff.setPreferredSize(new Dimension(tailleFenetre.width,
				outputHeight));
		panAff.setLayout(new BorderLayout());

		zoneAffichage = new JTextPane();
		zoneAffichage.setAutoscrolls(true);		
		zoneAffichage.setEditable(false);
		zoneAffichage.setForeground(backgroundColor);
		
		sc = new JScrollPane(zoneAffichage);
		sc.setPreferredSize(new Dimension(tailleFenetre.width, outputHeight));
		panAff.add(sc);
				
		panSaisie = new JPanel();
		panSaisie.setPreferredSize(new Dimension(tailleFenetre.width - 10,
				inputHeight));
		panSaisie.setLayout(new BorderLayout());
		
		zoneSaisie = new JTextPane();
		envoiMsg = new JButton("Send");
		
		zoneSaisie.setPreferredSize(new Dimension(320, inputHeight));
		zoneSaisie.addKeyListener(this);
		envoiMsg.addActionListener(this);

		panSaisie.add(zoneSaisie,BorderLayout.WEST);
		panSaisie.add(envoiMsg,BorderLayout.EAST);
	}
	
	private void send(String message) {
		this.client.handleMessageFromClientUI(message);
		this.zoneSaisie.setText("");
		this.setTitle("Client " + client.getId());
		
		if (message.equals("#logoff")) {
			goToLoginFrame();
		}
	}
	
	private void goToChatFrame(String login) {
		this.setTitle("Client " + login);
		panLogin.setVisible(false);
		panFenetre.remove(panLogin);
		panFenetre.setLayout(flowLayout);
		panFenetre.setBackground(null);
		panAff.setVisible(true);
		panSaisie.setVisible(true);
		panFenetre.add(this.panAff);
		panFenetre.add(this.panSaisie);
	}
	
	private void goToLoginFrame() {
		this.setTitle("Client");
		this.zoneAffichage.setText("");
		panAff.setVisible(false);
		panSaisie.setVisible(false);
		panFenetre.remove(this.panAff);		
		panFenetre.remove(this.panSaisie);			
		panFenetre.setLayout(borderLayout);
		panFenetre.setBackground(backgroundColor);
		panLogin.setVisible(true);
		panFenetre.add(this.panLogin, BorderLayout.CENTER);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == confirmLogin) {
			this.confirmLogin();
		}
		if (e.getSource() == envoiMsg) {
			send(this.zoneSaisie.getText());
		}
		if (e.getSource() == saisieLogin) {
			saisieLogin.setText("");
		}
	}

	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == '\n') {
			if (e.getSource() == this.zoneSaisie) {
				String msg = this.zoneSaisie.getText();
				send(msg.substring(0, msg.length() - 1));
			}
			else if (e.getSource() == this.confirmLogin
					|| e.getSource() == this.saisieLogin) {
				this.confirmLogin();
			}
		}
	}
	
	public void confirmLogin() {
		String login = saisieLogin.getText();
		String msg;
		saisieLogin.setText("");

		this.client.handleMessageFromClientUI("#login " + login);
		if (login.equals(this.client.getId())) {
			if(this.client.clientObs.isConnected()) {
				this.setTitle("Client " + login);
				goToChatFrame(login);
			}
			else {
				msg = "The server isn't currently connected.";
				JOptionPane.showMessageDialog(this, msg,
					      "Warning",
					      JOptionPane.WARNING_MESSAGE);
			}
		}
		else {
			msg = "You must enter a correct login.";
			JOptionPane.showMessageDialog(this, msg,
				      "Warning",
				      JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public void display(String message) {
		this.zoneAffichage.setText(this.zoneAffichage.getText() +
				'\n' + message);
		zoneAffichage.setCaretPosition(zoneAffichage.getText().length());
	}

	public void accept() {

	}
	
	public void mouseClicked(MouseEvent e) {

	}
	
	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent arg0) {
		
	}

	public void mouseReleased(MouseEvent arg0) {
		
	}

	public void keyPressed(KeyEvent arg0) {
		
	}

	public void keyReleased(KeyEvent arg0) {
		
	}

	public static void main(String[] args) {
		ClientGUI chat;
		
		String host = "";
  		String id = "";
  		int port = 0;  //The port number
  		
  		try {
  			id = args[0];
  		}
  		catch(ArrayIndexOutOfBoundsException e) {
  			id = "without id";
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
    
		chat = new ClientGUI(id, host, port);
  		
  		chat.accept(); //Put welcome message  		
  	}
}
