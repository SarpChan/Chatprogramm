package client_server;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Klasse, die von Thread erbt und Nachrichten sendet
 */
public class SendingThread extends Thread {
	
	private byte[] sendData = new byte[1024];
	private byte[] ok;
	private DatagramSocket clientSocket = null;
	private InetAddress IPAddress = null;
	private DatagramPacket sendPacket;
	private Client client;
	private int chatPort;
	
	/**
	 * Konstruktor
	 * @param client Client, in dem der ReceivingThread instanziiert wurde
	 * @param chatHost Host des Chatpartners
	 * @param chatPort Port des Chatpartners
	 * @param ok ok-Byte, das bei Empfang einer Nachricht gesendet wird
	 */
	public SendingThread(Client client, String chatHost, int chatPort, byte [] ok) {
		super();
		this.client = client;
		this.chatPort = chatPort;
		this.ok = ok;
		try {
			clientSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName(chatHost);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sendet ok-Byte, wenn der Client eine Nachricht empfangen hat
	 */
	@Override
	public void run() {
		
		while(!clientSocket.isClosed()) {
			try {
				if(client.isReceived()) { 
					sendPacket = new DatagramPacket(ok, ok.length, IPAddress, chatPort);
					clientSocket.send(sendPacket);
					client.setReceived(false);
					System.out.println("SENDING OK");
				} 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * sendet Nachricht an Chatpartner
	 * @param text Nachricht, die gesendet werden soll
	 */
	public void send(String text) {
		sendData = text.getBytes();

		client.getMsg().addMessage(client.getBenutzername(), text);

		for(int i = 0; i < 4; i++) {
			client.setSent(false);
			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, chatPort);
			System.out.println("SENDING: " + text);
			try {
				clientSocket.send(sendPacket);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(client.isSent())
				break;
			
		}
	}
	
	/**
	 * schliesst den Socket
	 */
	public void closeSocket() {
		clientSocket.close();
	}


}
