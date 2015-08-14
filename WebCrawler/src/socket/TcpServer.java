package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static common.LogManager.*;

public class TcpServer implements Runnable {
	private int m_portNum = 0;
	private ServerSocket m_server = null;

	private class ServerWorker implements Runnable {
		private Socket m_socket = null;
		
		ServerWorker(Socket socket) {
			m_socket = socket;
		}
		
		public void run() {
			try{
				BufferedReader in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
				PrintWriter out = new PrintWriter(m_socket.getOutputStream(), true);

				while(true){
					String line = in.readLine();
					out.println(line);
					
				}
			} catch (IOException e) {
				writeGenericLog("Fail to listen to request from client " + e.getMessage());
			} finally {
				try {
					writeGenericLog("Try to close socket connection " + m_socket.toString());
					m_socket.close();
				} catch (IOException e1) {
					writeGenericLog("Fail to close socket connection " + m_socket.toString() + ", error " + e1.getMessage());
				}
			}
		}
	}
	
	public TcpServer(int portNum) throws IOException {
		m_portNum = portNum;

		try{
			m_server = new ServerSocket(m_portNum); 
		} catch (IOException e) {
			System.out.println("Could not listen on port " + m_portNum);
		    throw e;
		}
	}

	public void run() {
		try {
			while (true) {
				try{
					ServerWorker newServerWorker = new ServerWorker(m_server.accept());
					newServerWorker.run();
				} catch (IOException e) {
					System.out.println("Accept failed: " + m_portNum);
					System.exit(-1);
				}
			}
		} finally {
			try {
				writeGenericLog("Try to close server " + m_server.toString());
				m_server.close();
			} catch (IOException e) {
				writeGenericLog("Fail to close server" + m_server.toString() + ", error " + e.getMessage());
			}
		}
	}
}
