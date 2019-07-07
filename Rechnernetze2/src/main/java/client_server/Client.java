package client_server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Java Client Klasse, die Methoden zur Kommunikation mit dem Server und anderen CLients umsetzt 
 */
public class Client {

	private Socket socket;
	private BufferedWriter writer;
	private String benutzername = "";
	private ReceivingThread receivingThread = null;
	private SendingThread sendingThread = null;
	private Thread serverThread = null;
	private ObservableListe activeUsers = new ObservableListe();
	private BooVariable loggedIn = new BooVariable(false);
	private BooVariable chatanfrage = new BooVariable(false);
	private Map<String, MessageListe> nachrichten = new HashMap<>();
	private ObservableString chatPartner = new ObservableString();
	private boolean received = false;
	private boolean sent = true;
	private MessageListe msg;
	private String chatpartnerName;
	


	/**
	 * Konstruktor des Clients
	 */
	public Client() {
		connectToServer();
	}

	/**
	 * baut Verbindung zum Server auf und erzeugt BufferedWriter für diese Verbindung
	 */
	private void connectToServer() {
		try {
			socket = new Socket("localhost", 27999);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * liest Nachrichten vom Server aus und ruft entsprechende Methoden zu deren Verarbeitung auf
	 * @param line Nachricht vom Server
	 */
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
		} else if (line.startsWith("10 ")) {
			chatanfrage.setBoo(false);
			endUdpConnection();
		}
	}

	/**
	 * erzeugt Thread, der konstant auf Nachrichten vom Server wartet
	 */
	public void startServerThread() {
		serverThread = new Thread() {
			BufferedReader serverReader;

			/**
			 * empfaengt Nachrichten vom Server und ruft processReceived() vom Client auf 
			 */
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

	/**
	 * Sendet eine Nachricht an den Server
	 * @param text Nachricht, die an den Server geschickt werden soll
	 */
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

	/**
	 * erzeugt Nachricht, die dem Server mitteilt, dass der Client sich abmelden möchte
	 */
	public void close() {
		try {
			System.out.println("Sende an Server: " + "3 " + benutzername);
			sendText("3 " + benutzername);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * erzeugt Nachricht, die bei Server UDP-Connection mit user "name" anfragen
	 * @param name Name des Users, mit dem Verbindung aufgebaut werden soll
	 */
	public void requestUdpConnection(String name) {
		sendText("2 " + name);
	}

	/**
	 * erzeugt Nachricht, die dem Server mitteilt, ob die Chatanfrage positiv oder negativ beantwortet wurde
	 * @param bool Antwort auf Chatanfrage 
	 */
	public void answerUdpConnection(boolean bool) {
		if(bool){
			sendText("8 " + chatanfrage.getVon());
		} else{
			sendText("9 " + chatanfrage.getVon());
		}
	}

	/**
	 * baut UDP-Connection auf
	 * erzeugt neue Message-Liste, sofern noch keine besteht oder holt sich die bestehende aus der Nachrichten-Map
	 * erzeugt einen ReceivingThread und einen SendingThread zur Kommunikation mit dem Chatpartner
	 * @param line vom Server übermittelter Port, Ip-Adresse und Name des Chatpartners sowie eigener Port
	 */
	public void buildUdpConnection(String line) {
		String [] data = line.split(" ");
		int chatPort = Integer.parseInt(data[1]);
		int meinPort = Integer.parseInt(data[3]);
		String chatHost = data[2];
		byte [] ok = hexStringToByteArray("0000004F004B0000");
		chatpartnerName = data[4];

		if(nachrichten.containsKey(benutzername + chatpartnerName))
			setMsg(nachrichten.get(benutzername + chatpartnerName));
		else {
			setMsg(new MessageListe(benutzername, chatpartnerName));
			nachrichten.put(benutzername + chatpartnerName, getMsg());
		}	
		this.chatPartner.setString(benutzername + chatpartnerName);

		receivingThread = new ReceivingThread(this, meinPort, chatpartnerName, ok); 
		receivingThread.start();

		sendingThread = new SendingThread(this, chatHost, chatPort, ok);
		sendingThread.start();
	}

	/**
	 * erzeugt Nachricht, die dem Server mitteilt, dass der Chat mit user "name" beendet werden soll
	 * ruft endUdpConnection() auf
	 * @param name Name des Chatpartners
	 */
	public void sendEndUdpConnection(String name) {
		sendText("10 " + name);
		endUdpConnection();
	}
	
	/**
	 * beendet die bestehende UDP-Connection
	 * beendet den ReceivingThread und den SendingThread und setzt booleans sent und received auf Ausgangswerte
	 */
	public void endUdpConnection() {
		sendingThread.closeSocket();
		receivingThread.closeSocket();
		sendingThread.interrupt();
		receivingThread.interrupt();
		received = false;
		sent = true;
	}

	/**
	 * erzeugt Nachricht, die dem Server die Login-Daten mitteilt
	 * Passwort wird dabei umgedreht und so "sicher" uebertragen
	 * @param username vom User eingegebener Username
	 * @param password vom User eingegebenes Passwort
	 * @param option Registrieren(0) oder Anmelden(1)
	 */
	public void login(String username, String password, String option) {
		final StringBuffer reverse = new StringBuffer(password).reverse();

		String line = option + " " + username + " " + reverse;
		sendText(line);

		benutzername = username;
	}

	/**
	 * erzeugt Nachricht, die dem Server mitteilt, dass der User sich abmelden und die Verbindung beenden will
	 */
	public void schliessen(){
		sendText("6 ");
	}

	/**
	 * wandelt einen hexadezimal-String in ein Byte-Array um
	 * @param s hexadezimal-String
	 * @return Byte-Array
	 */
	public byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[1024];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * main-Methode
	 * erzeugt einen Client und eine GUI und startet den Thread des Clients, der Nachrichten des Servers empfaengt
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final Client client = new Client();
		UI gui = new UI(client);

		client.startServerThread();

		gui.setVisible(true);
	}

	
	// Getter und Setter
	
	/**
	 * @return activeUsers
	 */
	public ObservableListe getActiveUsers() {
		return activeUsers;
	}

	/**
	 * @return boolscher Wert von loggedIn
	 */
	public boolean isLoggedIn() {
		return loggedIn.isBoo();
	}

	/**
	 * @return loggedIn
	 */
	public BooVariable getLoggedIn() {
		return this.loggedIn;
	}

	/**
	 * @return boolscher Wert von chatanfrage
	 */
	public boolean hasChatanfrage() {
		return this.chatanfrage.isBoo();
	}

	/**
	 * @return chatanfrage
	 */
	public BooVariable getChatanfrage() {
		return this.chatanfrage;
	}

	/**
	 * setzt boolschen Wert von chatanfrage
	 * @param chatanfrage ob eine Chatanfrage empfangen wurde
	 */
	public final void setChat(boolean chatanfrage) {
		this.chatanfrage.setBoo(chatanfrage);
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

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public MessageListe getMsg() {
		return msg;
	}

	public void setMsg(MessageListe msg) {
		this.msg = msg;
	}

	public String getBenutzername() {
		return benutzername;
	}

	public void setBenutzername(String benutzername) {
		this.benutzername = benutzername;
	}

	public ReceivingThread getReceivingThread() {
		return receivingThread;
	}
	
	public SendingThread getSendingThread() {
		return sendingThread;
	}

	public void setSendingThread(SendingThread sendingThread) {
		this.sendingThread = sendingThread;
	}
	
	public String getChatpartnerName() {
		return chatpartnerName;
	}

	public void setChatpartnerName(String chatpartnerName) {
		this.chatpartnerName = chatpartnerName;
	}


}
