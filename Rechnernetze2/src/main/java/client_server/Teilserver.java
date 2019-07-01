package client_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class Teilserver {

    private Map<String,String> nutzer;
	private Map <String, Socket> aktiveNutzer;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
	private Nutzerverwaltung nutzerverw;
	private String user;

    public Teilserver(Socket socket) {

        nutzerverw = Nutzerverwaltung.getInstance();
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.socket = socket;
			
        } catch (IOException e) {
            e.printStackTrace();
		}
		
		nutzerverw.getMap().setListener(new ObservableMap.ChangeListener(){
		
			@Override
			public void onChange() {
				handleActiveUserReq(user);
			}
		});
    }

    
    public synchronized void handleRequests()
	{
		while (!socket.isClosed())
		{
			String line;
			try
			{
				line = reader.readLine();
				System.out.printf("Vom Client (%s) empfangen: %s%n", socket.getRemoteSocketAddress(), line);

				// Registrieren
				String eingabe[] = line.split(" ");

				switch(eingabe[0]) {
				case "0":
					this.user = eingabe[1];
					handleRegistrieren(eingabe, socket);
					break;
				case "1":
					System.out.println(socket);
					this.user = eingabe[1];
					handleAnmelden(eingabe, socket);
					break;
				case "2":
					handleChat(eingabe, socket);
					break;
				case "3":
					handleAbmelden(eingabe);
					break;
				case "6":
					handleSchliessen(eingabe);
					break;
				default:
					System.out.println("default " + line);
					writer.write("default \n");
					writer.flush();
					break;
				}
				
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
				return;
			}
		}
    }
    
    
    private void handleActiveUserReq(String eingabe) {
		List <String> tempSet = getAktiveNutzer();
		try {
			writer.write("7 ");
			for (String element : tempSet) {
				if (!element.equalsIgnoreCase(eingabe)){
					writer.write(element + " ");
					System.out.println(element);
				}
			} 
			writer.write("\n");
			writer.flush();
		}
		catch(IOException e){
			e.printStackTrace();
		}
    }
    
    
    public void handleRegistrieren(String[] line, Socket socket) {
		String benutzername = line[1];
		String reverse = line[2];
		final StringBuffer passwort = new StringBuffer(reverse).reverse();

		if(!nutzerverw.compareUser(benutzername)) {
            nutzerverw.addUser(benutzername, passwort.toString());
			nutzerverw.addActiveUser(benutzername, socket);
			
			try {
				writer.write("0 200 \n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				writer.write("Nutzername bereits vergeben \n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    
    public void handleAnmelden(String []line, Socket socket) {
		String benutzername = line[1];
		String reverse = line[2];
		final StringBuffer passwort = new StringBuffer(reverse).reverse();
		
		try{
		if(nutzerverw.compareUser(benutzername)) {
			if(nutzerverw.comparePass(benutzername, passwort.toString())) {
				if(!nutzerverw.isUserActive(benutzername)) {
					System.out.println("Eingeloggt");
					
					nutzerverw.addActiveUser(benutzername, socket);
					writer.write("1 200 \n");
					
				} else {
					System.out.println("Schon eingeloggt ");
					
					writer.write("Schon eingeloggt \n");
					
				}
			} else {
				System.out.println("Passwort falsch ");
				
				writer.write("Passwort falsch \n");
				
			}
		} else {
			System.out.println("Nutzer nicht vorhanden");
			
			writer.write("Nutzer nicht vorhanden \n");
			
		}
		writer.flush();
		} catch (IOException e){
			e.printStackTrace();
		}
		
    }
    
    
    public void handleChat(String []line, Socket socket) {
		Socket chatPartnerSocket = nutzerverw.getActiveUserSocket(line[1]);
		int port = chatPartnerSocket.getPort();
		InetAddress ip = chatPartnerSocket.getInetAddress();
		String answer = "";
		
		try {
			BufferedReader chatPartnerReader = new BufferedReader(new InputStreamReader(chatPartnerSocket.getInputStream()));
			BufferedWriter chatPartnerWriter = new BufferedWriter(new OutputStreamWriter(chatPartnerSocket.getOutputStream()));
			
			chatPartnerWriter.write("5 " + user + " \n");
			chatPartnerWriter.flush();
			System.out.println("Chatanfrage gesendet");

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	
	public void handleAbmelden(String [] line) {
		String benutzername = line[1];
		nutzerverw.removeActiveUser(benutzername);
		try {
			writer.write("3 200" + "\n");
			writer.flush();

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleSchliessen(String [] line){
		String benutzername = line[1];
		nutzerverw.removeActiveUser(benutzername);
		try {
			writer.write("6 200" + "\n");
			writer.flush();
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	
    public Map<String, String> getNutzer() {
		return nutzer;
    }
    
    public List<String> getAktiveNutzer() {
		return nutzerverw.getActiveUserlist();
	}

	


}