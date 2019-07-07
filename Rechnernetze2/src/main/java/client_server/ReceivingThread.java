package client_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class ReceivingThread extends Thread {

	private byte[] receiveData = new byte[1024];
	private byte[] ok;
	private DatagramSocket clientSocket = null;
	private String chatpartner;
	private Client client;

	public ReceivingThread(Client client, int meinPort, String chatpartner, byte [] ok) {
		super();
		this.chatpartner = chatpartner;
		this.client = client;
		this.ok = ok;
		try {
			clientSocket = new DatagramSocket(meinPort);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void run() {

		while(!clientSocket.isClosed()) {
			receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);        
			try {
				clientSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}  
			if(!clientSocket.isClosed()) {
				if(Arrays.equals(receivePacket.getData(), ok)) {
					client.setSent(true);
					System.out.println("RECEIVED OK");
				} else {
					client.setReceived(true);
					String modifiedSentence = new String(receivePacket.getData());        
					System.out.println("FROM CHATPARTNER: " + modifiedSentence); 
					client.getActMessageListe().addMessage(chatpartner, modifiedSentence);
				}
			}
		}
	}

	public void closeSocket() {
		clientSocket.close();
	}


}
