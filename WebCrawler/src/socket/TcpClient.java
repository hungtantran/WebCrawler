package socket;

import static common.LogManager.writeGenericLog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import common.ErrorCode.CrError;

public class TcpClient implements Runnable {
	private String m_serverName = null;
	private int m_portNum = 0;
	private Socket m_client = null;
	private DataOutputStream m_out = null;
	private DataInputStream m_in = null;
	private IProcessPacket m_process = null;
	
	public TcpClient(String serverName, int portNum, IProcessPacket process) throws IOException {
		m_serverName = serverName;
		m_portNum = portNum;

		try {
			InetAddress inetAddress = InetAddress.getByName(serverName);
			m_client = new Socket(inetAddress, m_portNum);
			writeGenericLog("Successfully create client socket to " + m_client.toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			writeGenericLog("Fail to create client " + e.getMessage());
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			writeGenericLog("Fail to create client " + e.getMessage());
			throw e;
		}
		
		try{
			m_out = new DataOutputStream(m_client.getOutputStream());
			m_in = new DataInputStream(m_client.getInputStream());
		} catch (UnknownHostException e) {
			writeGenericLog("Unknown host: " + m_serverName + ", with error " + e.getMessage());
			throw e;
		} catch  (IOException e) {
			writeGenericLog("No I/O " + e.getMessage());
			throw e;
		}
		
		m_process = process;
	}
	
	public void run() {
		while (true) {
			try	{
				// Read length of incoming message
				int length = m_in.readInt();

				if(length > 0) {
				    byte[] packet = new byte[length];

				    // Read the message
				    m_in.readFully(packet, 0, packet.length);
				    
				    // Process the packet
				    synchronized(m_out) {
				    	m_process.process(packet, m_out);
				    }
				}
			} catch (IOException e) {
				e.printStackTrace();
				writeGenericLog("Fail to get packet from socket " + e.getMessage());
				return;
			}
		}
	}
	
	public CrError sendPacket(byte[] packet) {
		CrError hr = CrError.CR_OK;
		
		synchronized(m_out) {
			try {
				// Write length of message
				m_out.writeInt(packet.length);
				
				// Write message
				m_out.write(packet);
			} catch (IOException e) {
				e.printStackTrace();
				writeGenericLog("Fail to get send packet, with error " + e.getMessage());
				hr = CrError.CR_NETWORK_ERROR;
			}
		}
		
		return hr;
	}
	
	protected void finalize(){
		try{
			m_client.close();
	    } catch (IOException e) {
	    	writeGenericLog("Could not close client socket " + m_client.toString() + ", with error = " + e.getMessage());
	    }
	}
}
