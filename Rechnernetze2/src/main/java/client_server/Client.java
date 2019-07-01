package client_server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
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

// In der line steht 0: fuer registrieren
// 1: Anmelden
// 2: Chatten

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
			answerUdpConnection();
		} else if (line.startsWith("0 ") || line.startsWith("1 ")) {
			if (line.trim().endsWith("200")) {
				loggedIn.setBoo(true);
			} else
				loggedIn.setBoo(false);
		} else if (line.startsWith("2 ")) {
			buildUdpConnection(line);
		} else if (line.equals("3 200")) {

			loggedIn.setBoo(false);

		} else if (line.startsWith("7 ")) {
			System.out.println(line);
			String temp = line.substring(2);

			activeUsers.setListe(temp.split(" "));
		} else if (line.startsWith("6 ")) {
			loggedIn.setBoo(false);
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
	public void answerUdpConnection() {
		setChat(true);
	}

	public void buildUdpConnection(String line) {
		String [] data = line.split(" ");
		int chatPort = Integer.parseInt(data[1]);
		String chatHost = data[2];
		
		receivingThread = new Thread() {
			byte[] receiveData = new byte[1024];
			DatagramSocket clientSocket = null;

			@Override
			public void run() {
				try {
					clientSocket = new DatagramSocket();
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
				while(true) {
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);        
					try {
						clientSocket.receive(receivePacket);
					} catch (IOException e) {
						e.printStackTrace();
					}        
					String modifiedSentence = new String(receivePacket.getData());        
					System.out.println("FROM CHATPARTNER:" + modifiedSentence); 
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
						sentence = inFromUser.readLine();
						sendData = sentence.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, chatPort);
						System.out.println("SENDING" + sentence);
						clientSocket.send(sendPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}        
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
 /*
	public void requestActiveUser() {
		String line = "7 " + benutzername;
		sendText(line);
	}
*/
	public void login(String username, String password, String option) {
		String line;
		System.out.println("Registrieren");

		// "sichere" Übertragung des Passworts
		final StringBuffer reverse = new StringBuffer(password).reverse();

		line = option + " " + username + " " + reverse;
		sendText(line);

		benutzername = username;
	}

	public void schliessen(){
		sendText("6 ");
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
				gson.toJson(x, new FileWriter("../resources/" + x.getUser() + x.getOtherUser() + ".json"));
			} catch (JsonIOException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	public void loadChats() {

		final File folder = new File("../resources");

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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonIOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}

	}


	public List<Message> openChat(String key){

		return this.nachrichten.get(key).getListe() == null? null: this.nachrichten.get(key).getListe();

	}

	
}
