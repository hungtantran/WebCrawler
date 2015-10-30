package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import common.Helper;

public class TcpServer implements Runnable {
	private static Logger LOG = LogManager.getLogger(TcpServer.class.getName());

	private int m_portNum = 0;
	private ServerSocket m_server = null;
	private IProcessPacket m_process = null;

	private class ServerWorker implements Runnable {
		private Socket m_socket = null;
		private IProcessPacket m_process = null;
		private DataInputStream m_in = null;
		private DataOutputStream m_out = null;
		
		ServerWorker(Socket socket, IProcessPacket process) {
			m_socket = socket;
			m_process = process;

			try {
				m_in = new DataInputStream(m_socket.getInputStream());
				m_out = new DataOutputStream(m_socket.getOutputStream());
			} catch (IOException e) {
				LOG.error("Fail to listen to request from client " + e.getMessage());
			}
			
			LOG.info("Create new server worker with socket " + m_socket.toString());
		}
		
		public void run() {
			while(true){
				try	{
					// Read length of incoming message
					int length = m_in.readInt();

					if(length > 0) {
					    byte[] packet = new byte[length];

					    // Read the message
					    m_in.readFully(packet, 0, packet.length);
					    
					    // Process the packet
						m_process.process(packet, m_out);
					}
				} catch (IOException e) {
					e.printStackTrace();
					LOG.error("Fail to get packet from socket " + e.getMessage());
					return;
				}
			}
		}
	}
	
	public TcpServer(int portNum, IProcessPacket process) throws IOException {
		m_portNum = portNum;
		m_process = process;

		try{
			m_server = new ServerSocket(m_portNum);
			LOG.info("Successfully create server " + m_server.toString());
		} catch (IOException e) {
			LOG.error("Could not listen on port " + m_portNum);
		    throw e;
		}
	}

	public void run() {
		try {
			while (true) {
				try{
					if (m_server.isClosed()) {
						LOG.info("Socket is closed. Exit");
						return;
					}

					ServerWorker newServerWorker = new ServerWorker(m_server.accept(), m_process);
					newServerWorker.run();
				} catch (IOException e) {
					LOG.error("Accept failed: " + m_portNum);
					System.exit(-1);
				}
			}
		} finally {
			try {
				LOG.info("Try to close server " + m_server.toString());
				m_server.close();
			} catch (IOException e) {
				LOG.error("Fail to close server" + m_server.toString() + ", error " + e.getMessage());
			}
		}
	}
}
