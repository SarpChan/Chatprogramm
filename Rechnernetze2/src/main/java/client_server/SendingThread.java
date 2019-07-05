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
	private DatagramPacket sendPacket;
	private Client client;
	private int chatPort;
	
	
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
	
	@Override
	public void run() {
		
		while(true) {
			try {
				clientSocket.setSoTimeout(0);

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

}
