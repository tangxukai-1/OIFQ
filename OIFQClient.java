import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;

public class OIFQClient extends Thread
{
	public String serverName;
	public int port;
	public Socket socket;
	public DataOutputStream out;
	public DataInputStream in;
	private ExecutorService service;

	OIFQClient(String serverName, int port){
		try {
			serverName = serverName;
			this.port = port;
			socket = new Socket(serverName,port);
			OutputStream outToServer = socket.getOutputStream();
			out = new DataOutputStream(outToServer);
			InputStream inFromServer = socket.getInputStream();
			in = new DataInputStream(inFromServer);
			service = Executors.newFixedThreadPool(2);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	static class Handler implements Runnable {
		String type;
		DataOutputStream out;
		DataInputStream in;
		Socket socket;
		static int flag;

		public Handler(String type,DataOutputStream out,DataInputStream in,Socket socket,int flag) {
			this.type = type;
			this.in = in;
			this.out = out;
			this.socket = socket;
			this.flag =flag;
		}

		public void run() {
			if(type.equals("b")){
				try{	
					while(true){
						Scanner sysin = new Scanner(System.in);
						String a = sysin.nextLine();
						if(a.equals("exit")){
							out.writeUTF("exit");
							flag = 1;
							break;
						}
						out.writeUTF(a);
					}
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			if(type.equals("s")){
				try{
					while(flag != 1){
						System.out.println(in.readUTF());
					}
					socket.close();
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	

	public static void main(String [] args)
	{
		String serverName = args[0];
		int port = Integer.parseInt(args[1]);
		//try
		//{
			int flag = 0;
			OIFQClient client = new OIFQClient(serverName, port);
			client.service.execute(new Handler("b",client.out,client.in,client.socket,flag));
			client.service.execute(new Handler("s",client.out,client.in,client.socket,flag));
			//client.socket.close();
		/*}catch(IOException e)
		{
			e.printStackTrace();
		}*/
	}
}










