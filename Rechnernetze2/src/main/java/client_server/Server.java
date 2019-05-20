package client_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;


public class Server
{

	private Map<String,String> nutzer;
	public Server()
	{
		try
		{
			final ServerSocket serverSocket = new ServerSocket(27999);
			System.out.println("Warte auf Client...");
			final Socket socket = serverSocket.accept();
			System.out.printf("Client hat sich verbunden: %s%n", socket.getRemoteSocketAddress());

			final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			handleRequests(socket, reader, writer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void handleRequests(final Socket socket, final BufferedReader reader, final BufferedWriter writer)
	{
		while (true)
		{
			final String line;
			try
			{
				line = reader.readLine();
				System.out.printf("Vom Client (%s) empfangen: %s%n", socket.getRemoteSocketAddress(), line);
				// Registrieren
				String eingabe[] = line.split(" ");

				switch(eingabe[0]) {

				case "0":
					handleRegistrieren(eingabe);
					break;

				case "1":
					handleAnmelden(eingabe);
					break;
				}


				final StringBuffer reverse = new StringBuffer(line).reverse();
				System.out.printf("Sende an Client (%s): %s%n", socket.getRemoteSocketAddress(), reverse);
				writer.write(reverse + "\n");
				writer.flush();
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
				return;
			}
		}
	}

	public void handleRegistrieren(String []line) {
		String benutzername = line[1];
		String passwort = line[2];
		nutzer.put(benutzername, passwort);

	}
	public void handleAnmelden(String []line) {
		String benutzername = line[1];
		String passwort = line[2];
		if(nutzer.containsKey(benutzername)) {
			System.out.println("Nutzer vorhanden");
			if(nutzer.get(benutzername).equals(passwort)) {
				System.out.println("Eingeloggt");
			}
		}else
			System.out.println("Nutzer nicht vorhanden");
			
	}

	public static void main(String[] args){
		new Server();
	}
}
