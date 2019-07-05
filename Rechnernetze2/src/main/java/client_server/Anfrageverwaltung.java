package client_server;

import java.util.HashMap;
import java.util.Map;

public class Anfrageverwaltung {
	private Map<String, String> chatanfragen;
	private static Anfrageverwaltung anfrageverw = new Anfrageverwaltung();

	private Anfrageverwaltung() {
		this.chatanfragen = new HashMap<String,String>();
	}

	public static Anfrageverwaltung getInstance() {
		return anfrageverw;
	}

	public void addChatanfrage(String user1, String user2) {
		chatanfragen.put(user1, user2);
	}
	
	public String getChatanfrage(String key) {
		return chatanfragen.get(key);
	}
	
	public void removeChatanfrage(String key) {
		chatanfragen.remove(key);
	}


}
