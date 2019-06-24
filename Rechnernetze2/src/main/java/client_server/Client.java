package client_server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;


// In der line steht 0: fuer registrieren
// 1: Anmelden
// 2: Chatten

public class Client
{

	private Socket socket;
	private BufferedWriter writer;
	//	private BufferedReader reader;
	private String benutzername = "";
	private Thread receivingThread = null;
	private Thread sendingThread = null;
	private Thread serverThread = null;
	private String activeUsers = "";
	private boolean loggedIn = false;
	protected SimpleBooleanProperty chatanfrage;




	public class ServerThread extends Thread {
		BufferedReader serverReader;

		public ServerThread() {
			try {
				serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run()
		{
			String line = "";
			try {
				line = serverReader.readLine();
				System.out.println("Vom Server empfangen: " + line);
			} catch (IOException e) {
				e.printStackTrace();
			}
			processReceived(line);
		}
	}


	public Client()
	{
		this.chatanfrage = new SimpleBooleanProperty(false);
		connectToServer();
	}

	private void connectToServer(){
		try
		{
			socket = new Socket("localhost", 27999);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			//			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	private synchronized void processReceived(String line) 
	{
		if (line.equals("Neue Chatanfrage")) {
			answerUdpConnection();
		} else if (line.startsWith("0 ") || line.startsWith("1 ")) {
			if (line.trim().endsWith("200")) {
				loggedIn = true;
			} else
				loggedIn = false;
		} else if (line.startsWith("2 ")) {
			buildUdpConnection(line);
		} else if (line.equals("3 200")){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			loggedIn = false;
		} else if (line.startsWith("7 ")) {
			activeUsers = line.substring(1);
		}
	}


	public void sendText(final String text)
	{
		try
		{
			System.out.println("Sende an Server: " + text);
			writer.write(text + " \n");
			writer.flush();
		} catch(SocketException e){
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} 
	}


	public void close()
	{
		try
		{
			System.out.println("Sende an Server: " + "3 " + benutzername);
			sendText("3 " + benutzername);
		} catch(NullPointerException e){
			e.printStackTrace();
		}
	}


	//	bei Server UDP-Connection mit user "name" anfragen
	public void requestUdpConnection(String name) {	
		sendText("2 " + name);
	}


	//	durch Server uebermittelte Chatanfrage beantworten
	public void answerUdpConnection() {	
		setChat(true);
	}


	public void buildUdpConnection(String line) {
		int chatPort = -1;
		String chatHost = "";	

		receivingThread = new Thread() {
			byte[] receiveData = new byte[1024];
			DatagramSocket clientSocket = null;

			@Override
			public void run()
			{
				try {
					clientSocket = new DatagramSocket();
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);        
				try {
					clientSocket.receive(receivePacket);
				} catch (IOException e) {
					e.printStackTrace();
				}        
				String modifiedSentence = new String(receivePacket.getData());        
				System.out.println("FROM SERVER:" + modifiedSentence);       
			}
		};
		receivingThread.start();

		sendingThread = new Thread() {
			byte[] sendData = new byte[1024]; 
			DatagramSocket clientSocket = null;
			InetAddress IPAddress = null;

			@Override
			public void run()
			{
				try {
					clientSocket = new DatagramSocket();
					IPAddress = InetAddress.getByName(chatHost);
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				String sentence = "";
				try {
					sentence = inFromUser.readLine();
					sendData = sentence.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, chatPort);        
					clientSocket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}        
			}
		};
		sendingThread.start();
	}


	public void endUdpConnection(Socket clientSocket) {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void requestActiveUser(){
		String line ="7 " + benutzername;
		sendText(line);
	}


	public void login(String username, String password, String option) {
		String line;
		System.out.println("Registrieren");

		// "sichere" Übertragung des Passworts
		final StringBuffer reverse = new StringBuffer(password).reverse();

		line = option + " " + username + " " + reverse;
		sendText(line);

		benutzername = username;
	}


	public static void main(String[] args) throws IOException
	{
		final Client client = new Client();
		UI gui = new UI(client);

		client.serverThread = new Thread() {
			BufferedReader serverReader;

			@Override
			public void run()
			{
				while(true) {
					String line = "";
					try {
						serverReader = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
						line = serverReader.readLine();
						System.out.println("Vom Server empfangen: " + line);
					} catch (IOException e) {
						e.printStackTrace();
					}
					client.processReceived(line);
				}
			}
		};
		client.serverThread.start();
		
		gui.setVisible(true);
	}


	public String getActiveUsers() {
		return activeUsers;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}


	public final SimpleBooleanProperty chatProperty() {
		return this.chatanfrage;
	}

	public final Boolean getChat() {
		return this.chatProperty().get();
	}

	public final void setChat(boolean chatanfrage) {
		this.chatProperty().set(chatanfrage);
	}
}
