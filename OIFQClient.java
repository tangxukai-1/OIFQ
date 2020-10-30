import java.net.*;
import java.io.*;
import java.util.*;

public class OIFQClient extends Thread
{
	private String serverName;
	private int port;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;

	OIFQClient(String serverName, int port){
		try {
			serverName = serverName;
			this.port = port;
			socket = new Socket(serverName,port);
			OutputStream outToServer = socket.getOutputStream();
			out = new DataOutputStream(outToServer);
			InputStream inFromServer = socket.getInputStream();
			in = new DataInputStream(inFromServer);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String [] args)
	{
		String serverName = args[0];
		int port = Integer.parseInt(args[1]);
		try
		{
			OIFQClient client = new OIFQClient(serverName, port);
			Scanner sysin = new Scanner(System.in);
			while(true)
			{
				String a = sysin.nextLine();
				if(a.equals("exit")){
					client.out.writeUTF(a);
					break;
				}
				client.out.writeUTF(a);
				System.out.println(client.in.readUTF());
			}
			client.socket.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
