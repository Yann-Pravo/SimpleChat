

import javax.swing.*;

import serveur.EchoServer;

import common.ChatIF;

import java.awt.*;
import java.awt.event.*;

public class ServerGUI extends JFrame implements MouseListener, ActionListener,
		KeyListener, ChatIF {

	private static final long serialVersionUID = 1L;

	JPanel panFenetre;

	JPanel panStart;

	JPanel panAff;
	JPanel panSaisie;

	BorderLayout borderLayout;
	FlowLayout flowLayout;
	Dimension tailleFenetre;
	
	JLabel askStart;
	JButton confirmStart;
	JButton envoiMsg;
		
	JTextPane zoneSaisie;
	JTextPane zoneAffichage;
	JMenuItem itemStart;
	JScrollPane sc;

	Color backgroundColor = new Color(27,109,182);

	final public static int DEFAULT_PORT = 5555;
	EchoServer server;
	
	public ServerGUI(int port) {
		
		Dimension dimensions = (Toolkit.getDefaultToolkit()).getScreenSize();
		int hauteurEcran = dimensions.height;
		int largeurEcran = dimensions.width;
		tailleFenetre = new Dimension (400, 360);
		this.setSize(tailleFenetre);
		this.setResizable(false);
		this.setLocation(2 * largeurEcran / 3, hauteurEcran / 4);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		borderLayout = new BorderLayout();
		flowLayout = new FlowLayout();
		
		fillStartFrame();
		fillChatFrame();
		
		this.setVisible(true);
		
		server = new EchoServer(port, this);
	}
	
	public void fillStartFrame() {
		int inputHeight = 20;
		int verticalPosition = tailleFenetre.width / 2 - 80;
		
		this.setTitle("Server");
		panFenetre = new JPanel();
		panFenetre.setBackground(backgroundColor);
		panFenetre.setLayout(borderLayout);
		
		askStart = new JLabel("Welcome Server, please click to proceed");
		confirmStart = new JButton("Start");

		confirmStart.addActionListener(this);
		confirmStart.addKeyListener(this);

		askStart.setBounds(30, 20, 340, inputHeight);
		confirmStart.setBounds(verticalPosition, askStart.getHeight() + 30, 150,
				inputHeight);
		
		panStart = new JPanel();
		panStart.setBackground(backgroundColor);
		panStart.setLayout(null);
		panStart.add(askStart);
		panStart.add(confirmStart);
		
		panFenetre.add(panStart,BorderLayout.CENTER);

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
		this.server.handleMessageFromServerUI(message);
		this.zoneSaisie.setText("");
		
		if (message.equals("#close")) {
			goToStartFrame();
		}
	}
	
	private void start() {
		this.server.handleMessageFromServerUI("#start");
	}
	
	private void goToChatFrame() {
		panStart.setVisible(false);
		panFenetre.remove(panStart);
		panFenetre.setLayout(flowLayout);
		panFenetre.setBackground(null);
		panAff.setVisible(true);
		panSaisie.setVisible(true);
		panFenetre.add(this.panAff);
		panFenetre.add(this.panSaisie);
	}
	
	private void goToStartFrame() {
		this.zoneAffichage.setText("");
		panAff.setVisible(false);
		panSaisie.setVisible(false);
		panFenetre.remove(this.panAff);			
		panFenetre.remove(this.panSaisie);			
		panFenetre.setLayout(borderLayout);
		panFenetre.setBackground(backgroundColor);
		panStart.setVisible(true);
		panFenetre.add(panStart,BorderLayout.CENTER);
	}
	

	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == '\n') {
			if (e.getSource() == this.zoneSaisie) {
				String msg = this.zoneSaisie.getText();
				send(msg.substring(0, msg.length() - 1));
			}
			else if (e.getSource() == this.confirmStart) {
				this.confirmStart();
			}
		}
	}
	
	public void confirmStart() {
		start();
		goToChatFrame();
	}
	
	public void display(String message) {
		this.zoneAffichage.setText(this.zoneAffichage.getText()
				+ '\n' + message);
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

@Override
public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == confirmStart || e.getSource() == this.itemStart) {
			this.confirmStart();
		}
	}
	
	public static void main(String[] args) {
		ServerGUI sv;
		
	  	int port = 0; //Port to listen on
		
	  	try {
		  	port = Integer.parseInt(args[0]); //Get port from command line
	  	}
	  	catch(Throwable t) {
		  	port = DEFAULT_PORT; //Set port to 5555
	  	}
		
		sv = new ServerGUI(port);
  		
  		sv.accept(); //Put welcome message
  	}
}


