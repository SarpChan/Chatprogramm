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
		UI gui = new UI(client);

		gui.setVisible(true);

		//final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		//client.login(reader,"0 ");
		// while (true)
		// {
		// 	client.login(reader,"1 ");
		// 	String line = reader.readLine();
		// 	client.sendText(line);
		// }
	}

	
	public boolean login(String username, String password, String option) {
		String line;
		System.out.println("Registrieren");

		// "sichere" Übertragung des Passworts
		final StringBuffer reverse = new StringBuffer(password).reverse();

		line = option + " " + username + " " + reverse;

		try
		{
			System.out.println("Sende an Server: " + line);
			writer.write(line + "\n");
			writer.flush();

			final String text = reader.readLine();
			System.out.println("Vom Server empfangen: " + text);
			if (text.trim().equals("200")) {
				benutzername = username;
				return true;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return false;
	}
}
