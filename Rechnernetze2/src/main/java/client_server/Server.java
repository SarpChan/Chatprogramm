package client_server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;


public class Server
{
	
	private Map<InetAddress, Teilserver> teilserverliste;
	
	

	public Server()
	{
		
		teilserverliste = new HashMap<>();

		try
		{
			final ServerSocket serverSocket = new ServerSocket(27999);
			System.out.println("Warte auf Client...");
			while (true)
			{
				try
				{
					final Socket socket = serverSocket.accept();					

					final Thread thread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							InetAddress temp = socket.getInetAddress();
							teilserverliste.put(temp, new Teilserver(socket));
							teilserverliste.get(temp).handleRequests();
							teilserverliste.remove(temp);



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

	public void updateClients(ArrayList list){
		
	}


	public static void main(String[] args){
		new Server();
	}

	

	
	

	
		
	
	
	
	
	
	
	
	
	
	

	
}
