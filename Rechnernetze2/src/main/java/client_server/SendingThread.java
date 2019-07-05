package client_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SendingThread extends Thread {
	
	private byte[] sendData = new byte[1024];
	private byte[] ok;
	private DatagramSocket clientSocket = null;
	private InetAddress IPAddress = null;
	private BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	private String sentence = "";
	private DatagramPacket sendPacket;
	private Client client;
	private int chatPort;
	
	
	public SendingThread(Client client, String chatHost, int chatPort) {
		super();
		this.client = client;
		this.chatPort = chatPort;
		ok = client.hexStringToByteArray("0000004F004B0000");
		try {
			clientSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName(chatHost);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
				clientSocket.setSoTimeout(0);

				if(client.isReceived()) { 
					System.out.println("SENDING OK");
					client.setReceived(false);
					sendPacket = new DatagramPacket(ok, ok.length, IPAddress, chatPort);
					clientSocket.send(sendPacket);
				} else {
					sentence = inFromUser.readLine();
					sendData = sentence.getBytes();

					client.getMsg().addMessage(client.getBenutzername(), sentence);
					client.setSent(false);

					for(int i = 0; i < 4; i++) {
						sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, chatPort);
						System.out.println("SENDING: " + sentence);
						clientSocket.send(sendPacket);
						
						if(client.isSent())
							break;
						try {
							sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
