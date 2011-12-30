using System.Net.Sockets;
using System.Net;
using System.Threading;
using System.Text;
using UnityEngine;

public class SocketServer
{
	public event System.Action<string> ReadString;
	
	private readonly int bufferSize = 2048;
	private TcpListener socketServer;
	private bool threadStop;
	
	public SocketServer(int port)
	{ 
		socketServer = new TcpListener(IPAddress.Loopback, port);
		socketServer.Start();
		new Thread(new ThreadStart(Run)).Start();
	}
	
	private void Run()
	{
		while (true)
		{
			if (threadStop)
				return;
			if (!socketServer.Pending())
				continue;
			TcpClient client = socketServer.AcceptTcpClient();
			NetworkStream stream = client.GetStream();
			string result = "";
			int read = 0;
			do
			{
				byte[] buffer = new byte[bufferSize];
				read += stream.Read(buffer, read, bufferSize);
				result += Encoding.UTF8.GetString(buffer, 0, (read == bufferSize) ? bufferSize : read % bufferSize);
			}
			while (read % bufferSize == 0);
			if (ReadString != null)
				ReadString(result);
		}
	}
	
	public void Stop()
	{ threadStop = true; socketServer.Stop(); }
}
