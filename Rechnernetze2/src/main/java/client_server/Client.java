package client_server;

import java.io.*;
import java.net.Socket;


// In der line steht 0: fuer registrieren
// 1: Anmelden
// 2: Chatten

public class Client
{

	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private String benutzername = "";
	
	
	public Client()
	{
		try
		{
			socket = new Socket("localhost", 27999);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void sendText(final String text)
	{
		try
		{
			System.out.println("Sende an Server: " + text);
			writer.write(text + "\n");
			writer.flush();

			final String line = reader.readLine();
			System.out.println("Vom Server empfangen: " + line);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException
	{
		final Client client = new Client();
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		client.login(reader,"0 ");
		while (true)
		{
			client.login(reader,"1 ");
			String line = reader.readLine();
			client.sendText(line);
		}
	}

	public void login(BufferedReader reader, String option) {
		String line;
		System.out.println("Registrieren");
		try {
			System.out.println("Benutzername:");
			String lineName = reader.readLine();
			
			System.out.println("Passwort:");
			String linePasswort = reader.readLine();
			
			line = option + lineName + " " + linePasswort;
			
			sendText(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
