package client_server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;


public class Client {

	private Socket socket;
	private BufferedWriter writer;
	private String benutzername = "";
	private Thread receivingThread = null;
	private Thread sendingThread = null;
	private Thread serverThread = null;
	private ObservableListe activeUsers = new ObservableListe();
	private BooVariable loggedIn = new BooVariable(false);
	private BooVariable chatanfrage = new BooVariable(false);
	private Map<String, MessageListe> nachrichten = new HashMap<>();
	private ObservableString chatPartner = new ObservableString();
	private boolean received = true;


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
		public void run() {
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

	public Client() {
		connectToServer();
	}

	private void connectToServer() {
		try {
			socket = new Socket("localhost", 27999);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void processReceived(String line) {
		if (line.startsWith("5 ")) {
			this.chatanfrage.setVon(line.split(" ")[1]);
			setChat(true);
		} else if (line.startsWith("0 ") || line.startsWith("1 ")) {
			if (line.trim().endsWith("200"))
				loggedIn.setBoo(true);
			else
				loggedIn.setBoo(false);
		} else if (line.startsWith("2 ")) {
			buildUdpConnection(line);
		} else if (line.equals("3 200")) {
			loggedIn.setBoo(false);
		} else if (line.startsWith("7 ")) {
			String temp = line.substring(2);
			activeUsers.setListe(temp.split(" "));
		} else if (line.startsWith("6 ")) {
			loggedIn.setBoo(false);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void startServerThread() {
		serverThread = new Thread() {
			BufferedReader serverReader;

			@Override
			public void run()
			{
				while(true) {
					String line = "";
					try {
						serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						line = serverReader.readLine();
						System.out.println("Vom Server empfangen: " + line);
					} catch (IOException e) {
						e.printStackTrace();
					}
					processReceived(line);
				}
			}
		};
		serverThread.start();
	}


	public void sendText(final String text)
	{
		try
		{
			System.out.println("Sende an Server: " + text);
			writer.write(text + " \n");
			writer.flush();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void close() {
		try {
			System.out.println("Sende an Server: " + "3 " + benutzername);
			sendText("3 " + benutzername);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}


	// bei Server UDP-Connection mit user "name" anfragen
	public void requestUdpConnection(String name) {
		sendText("2 " + name);
	}


	// durch Server uebermittelte Chatanfrage beantworten
	public void answerUdpConnection(boolean bool) {
		if(bool){
			sendText("8 " + chatanfrage.getVon());
		} else{
			sendText("9 " + chatanfrage.getVon());
		}
	}


	public void buildUdpConnection(String line) {
		String [] data = line.split(" ");
		int chatPort = Integer.parseInt(data[1]);
		int meinPort = Integer.parseInt(data[3]);
		String chatHost = data[2];

		byte[] ok = hexStringToByteArray("0000004F004B0000");

		String chatpartner = data[4];
		MessageListe msg;
		//saveChats();
		loadChats();
		
		if(nachrichten.containsKey(benutzername + chatpartner)){
			msg = nachrichten.get(benutzername + chatpartner);
			this.chatPartner.setString(benutzername + chatpartner);
		}else {
			msg = new MessageListe(benutzername, chatpartner);
			nachrichten.put(benutzername + chatpartner, msg);
			this.chatPartner.setString(benutzername + chatpartner);
		}	

		receivingThread = new Thread() {
			byte[] receiveData = new byte[1024];
			DatagramSocket clientSocket = null;

			@Override
			public void run() {
				try {
					clientSocket = new DatagramSocket(meinPort);
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
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
					if(receivePacket.getData() != ok)
						msg.addMessage(chatpartner, modifiedSentence);
					else
						received = true;
				}
			}
		};
		receivingThread.start();

		sendingThread = new Thread() {
			byte[] sendData = new byte[1024];
			DatagramSocket clientSocket = null;
			InetAddress IPAddress = null;

			@Override
			public void run() {
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
				while(true) {
					try {
						clientSocket.setSoTimeout(0);
						sentence = inFromUser.readLine();
						sendData = sentence.getBytes();
						if(sendData != ok) {
							msg.addMessage(benutzername, sentence);
							received = false;
						} 
						for(int i = 0; i < 4; i++) {
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, chatPort);
							System.out.println("SENDING: " + sentence);
							clientSocket.send(sendPacket);
							if(received)
								break;
							try {
								sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		sendingThread.start();
	}


	public void endUdpConnection(Socket clientSocket) {
		sendingThread.interrupt();
		receivingThread.interrupt();
		saveChats();
	}


	public void login(String username, String password, String option) {
		String line;

		// "sichere" Übertragung des Passworts
		final StringBuffer reverse = new StringBuffer(password).reverse();

		line = option + " " + username + " " + reverse;
		sendText(line);

		benutzername = username;
	}


	public void schliessen(){
		sendText("6 ");
	}


	public byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}


	public static void main(String[] args) throws IOException {
		final Client client = new Client();
		UI gui = new UI(client);

		client.startServerThread();

		gui.setVisible(true);
	}

	public ObservableListe getActiveUsers() {
		return activeUsers;
	}

	public boolean isLoggedIn() {
		return loggedIn.isBoo();
	}

	/**
	 * @return the loggedIn
	 */
	public BooVariable getLoggedIn() {
		return this.loggedIn;
	}

	/**
	 * @return the chatanfrage
	 */
	public boolean hasChatanfrage() {
		return this.chatanfrage.isBoo();
	}

	public BooVariable getChatanfrage() {
		return this.chatanfrage;
	}

	public final void setChat(boolean chatanfrage) {
		this.chatanfrage.setBoo(chatanfrage);
	}

	public void saveChats() {
		for (MessageListe x : this.nachrichten.values()) {

			Gson gson = null;
			gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			try {
				gson.toJson(x, new FileWriter("Rechnernetze2/src/main/java/resources" + x.getUser() + x.getOtherUser() + ".json"));
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void loadChats() {

		final File folder = new File("Rechnernetze2/src/main/java/resources");

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				continue;
			} else {
				if (fileEntry.getName().endsWith(".json")) {
					Gson gson = new Gson();

					try {
						MessageListe list = gson.fromJson(new FileReader(fileEntry), MessageListe.class);
						this.nachrichten.put(list.getUser() + list.getOtherUser(), list);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					} catch (JsonIOException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	public List<Message> openChat(String key){
		return this.nachrichten.get(key).getListe() == null? null: this.nachrichten.get(key).getListe();
	}

	public ObservableString getObservableString(){
		return this.chatPartner;
	}

	public MessageListe getActMessageListe() {

		return nachrichten.get(this.chatPartner.getString());
	}

	public void removeMessageListeners() {
	}


}
