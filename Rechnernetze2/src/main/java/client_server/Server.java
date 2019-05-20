package client_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
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

	public static void main(String[] args)
	{
		new Server();
	}
}
