package client_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceivingThread extends Thread {

	private byte[] receiveData = new byte[1024];
	private byte[] ok;
	private DatagramSocket clientSocket = null;
	private String chatpartner;
	private Client client;
	
	public ReceivingThread(Client client, int meinPort, String chatpartner) {
		super();
		this.chatpartner = chatpartner;
		this.client = client;
		ok = client.hexStringToByteArray("0000004F004B0000");
		try {
			clientSocket = new DatagramSocket(meinPort);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		while(true) {
			receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);        
			try {
				clientSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}        
			String modifiedSentence = new String(receivePacket.getData());        
			System.out.println("FROM CHATPARTNER: " + modifiedSentence); 
			if(receivePacket.getData() != ok) {
				client.getMsg().addMessage(chatpartner, modifiedSentence);
				client.setReceived(true);
				System.out.println("RECEIVED MESSAGE");
			} else {
				System.out.println("RECEIVED OK");
				client.setSent(true);
			}
		}
	}

}
