import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OIFQServer extends Thread
{
	private ServerSocket serverSocket;
	private int count;
	private Socket[] server;
	private DataInputStream[] in;
	private DataOutputStream[] out;
	private ExecutorService service;
	//private int[][] bindlist = new int[4][2];
	
	public OIFQServer(int port) throws IOException
	{
		serverSocket = new ServerSocket(port,5);
		//serverSocket.setSoTimeout(10000);
		count = 0;
		server = new Socket[2];
		in = new DataInputStream[2];
		out = new DataOutputStream[2];
		service = Executors.newFixedThreadPool(2);
		//bindlist[0]=bindlist[1]=0;
	}
	public void establish()
	{
		while(true)
		{
			try
			{
				server[count] = serverSocket.accept();
				in[count] = new DataInputStream(server[count].getInputStream());
				out[count] = new DataOutputStream(server[count].getOutputStream());
				count++;
				if(count==2)
					break;
			}catch(IOException e)
        	{
            	e.printStackTrace();
				break;
        	}/*catch(SocketTimeoutException s)
        	{
            	System.out.println("Socket timed out!");
            	break;
        	}*/
		}
		count=0;
		while(true)
		{
			service.execute(new Handler(server,in,out,count));
			count++;
			if(count==2)
				break;
		}
	}
	static class Handler implements Runnable {
		Socket socket = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		int bind;

		public Handler(Socket[] socket, DataInputStream[] in, DataOutputStream[] out, int count) {
			this.socket = socket[count];
			this.bind = 1 - count;
			this.in = in[count];
			this.out = out[bind];
		}

		public void run() {
			try {
				String readMessage = null;
				while (true) {
					System.out.println((1 - bind) + "server reading... ");
					readMessage = in.readUTF();
					System.out.println("RECIEVE:" + readMessage);
					if (readMessage.equals("exit")) {
						//System.out.println("EXIT");
						//out.writeUTF("The other side closed,enter exit!");
						break;
					}
					if (readMessage != null) {
						out.writeUTF(readMessage);
					}
				}
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public static void main(String []args)throws Exception
	{
		int port = Integer.parseInt(args[0]);
		try
		{
			OIFQServer will = new OIFQServer(port);
			System.out.println("server started!");
			will.establish();

		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
		
