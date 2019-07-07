package client_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Klasse, die von Thread erbt und Nachrichten empfaengt
 */
public class ReceivingThread extends Thread {

	private byte[] receiveData = new byte[1024];
	private byte[] ok;
	private DatagramSocket clientSocket = null;
	private String chatpartner;
	private Client client;

	/**
	 * Konstruktor
	 * @param client Client, in dem der ReceivingThread instanziiert wurde
	 * @param meinPort Port des Clients
	 * @param chatpartner Name des Chatpartners des Clients
	 * @param ok ok-Byte, das bei Empfang einer Nachricht gesendet wird
	 */
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

	/**
	 * Wartet auf eingehende Nachrichten und fuegt diese der MessageListe des Clients an
	 */
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
				} else {
					client.setReceived(true);
					String modifiedSentence = new String(receivePacket.getData());        
					client.getActMessageListe().addMessage(chatpartner, modifiedSentence);
				}
			}
		}
	}

	/**
	 * schliesst den Socket
	 */
	public void closeSocket() {
		clientSocket.close();
	}


}
