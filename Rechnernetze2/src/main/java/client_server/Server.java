package client_server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;


public class Server
{
	private Map<String,String> nutzer;
	private ArrayList <String> aktiveNutzer;
	private BufferedWriter writer;
	BufferedReader reader;

	public Server()
	{
		nutzer = new HashMap<>();
		aktiveNutzer = new ArrayList<>();

		try
		{
			final ServerSocket serverSocket = new ServerSocket(27999);
			System.out.println("Warte auf Client...");
			while (true)
			{
				try
				{
					final Socket socket = serverSocket.accept();
					System.out.println("Client hat sich verbunden: " + socket.getInetAddress());

					final Thread thread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
								writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

								handleRequests(socket, reader, writer);
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					});
					thread.start();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
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
			String line;
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
				case "3":
					handleAbmelden(eingabe);
					break;
				default:
					System.out.println(line);
					writer.write("default \n");
					break;
				}
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
		String reverse = line[2];
		final StringBuffer passwort = new StringBuffer(reverse).reverse();

		if(!nutzer.containsKey(benutzername) ) {
			nutzer.put(benutzername, passwort.toString());
			aktiveNutzer.add(benutzername);
			try {
				writer.write("200 \n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				writer.write("Nutzername bereits vergeben \n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
	
	public void handleAnmelden(String []line) {
		String benutzername = line[1];
		String reverse = line[2];
		final StringBuffer passwort = new StringBuffer(reverse).reverse();
		
		if(nutzer.containsKey(benutzername)) {
			if(nutzer.get(benutzername).equals(passwort.toString())) {
				if(!aktiveNutzer.contains(benutzername)) {
					System.out.println("Eingeloggt");
					try {
						aktiveNutzer.add(benutzername);
						writer.write("200 \n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Schon eingeloggt ");
					try {
						writer.write("Schon eingeloggt \n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("Passwort falsch ");
				try {
					writer.write("Passwort falsch \n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Nutzer nicht vorhanden");
			try {
				writer.write("Nutzer nicht vorhanden \n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	
	public void handleAbmelden(String [] line) {
		String benutzername = line[1];
		aktiveNutzer.remove(benutzername);
	}

	
	public static void main(String[] args){
		new Server();
	}
}
