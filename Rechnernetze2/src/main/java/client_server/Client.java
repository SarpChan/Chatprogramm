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
			
		}catch(SocketException e){
			
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

		}
			
		
		
	}

	
	public void udpConnection(String name) {	
		int port = -1;
		String host = "";
		
			
			
			

		final String text = sendText("2 " + name );
		

		String [] s = text.split(" ");
		port = Integer.parseInt(s[0]);
		host = s[1];
		
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		        
		 try {
			DatagramSocket clientSocket = new DatagramSocket(); 
			InetAddress IPAddress = InetAddress.getByName(host);
			byte[] sendData = new byte[1024];        
			byte[] receiveData = new byte[1024];        
			String sentence = inFromUser.readLine();        
			sendData = sentence.getBytes();
			 
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);        
			clientSocket.send(sendPacket);        
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);        
			clientSocket.receive(receivePacket);        
			String modifiedSentence = new String(receivePacket.getData());        
			System.out.println("FROM SERVER:" + modifiedSentence);        
			clientSocket.close(); 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
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
