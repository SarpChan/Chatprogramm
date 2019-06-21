package client_server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


// In der line steht 0: fuer registrieren
// 1: Anmelden
// 2: Chatten

public class Client
{

	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private String benutzername = "";
	private Thread receivingThread = null;
	private Thread sendingThread = null;


	public Client()
	{
		connectToServer();
	}

	private void connectToServer(){
		try
		{
			socket = new Socket("localhost", 27999);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public String sendText(final String text)
	{
		String line = "";
		try
		{
			System.out.println("Sende an Server: " + text);
			writer.write(text + " \n");
			writer.flush();

			line = reader.readLine();
			System.out.println("Vom Server empfangen: " + line);

		} catch(SocketException e){
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		return line;
	}


	public void close()
	{
		try
		{
			System.out.println("Sende an Server: " + "3 " + benutzername);
			final String text = sendText("3 " + benutzername);

			if(text.equals("200")){
				socket.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} catch(NullPointerException e){
			e.printStackTrace();
		}
	}


	public void requestUdpConnection(String name) {	
		final String text = sendText("2 " + name);

		String [] s = text.split(" ");

		int chatPort = -1;
		String chatHost = "";	

		buildUdpConnection(chatPort, chatHost);
	}


	public void answerUdpConnection() {	

	}


	public void buildUdpConnection(Integer chatPort, String chatHost) {

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


	public String requestActiveUser(){
		String line ="7 " + benutzername;

		final String text = sendText(line);

		return text;	
	}


	public boolean login(String username, String password, String option) {
		String line;
		System.out.println("Registrieren");

		// "sichere" Übertragung des Passworts
		final StringBuffer reverse = new StringBuffer(password).reverse();

		line = option + " " + username + " " + reverse;

		final String text = sendText(line);

		if (text.trim().equals("200")) {
			benutzername = username;
			return true;
		}
		return false;
	}


	public static void main(String[] args) throws IOException
	{
		final Client client = new Client();
		UI gui = new UI(client);

		gui.setVisible(true);
	}

}
