package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import static common.LogManager.*;

public class TcpClient implements Runnable {
	private String m_serverName = null;
	private int m_portNum = 0;
	private Socket m_client = null;
	
	public TcpClient(String serverName, int portNum) throws IOException {
		m_serverName = serverName;
		m_portNum = portNum;

		try {
			m_client = new Socket(m_serverName, m_portNum);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			writeGenericLog("Fail to create client " + e.getMessage());
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			writeGenericLog("Fail to create client " + e.getMessage());
			throw e;
		}
	}
	
	public void run() {
		try{
			PrintWriter out = new PrintWriter(m_client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(m_client.getInputStream()));
			// TODO do something with in and out
	   } catch (UnknownHostException e) {
		   writeGenericLog("Unknown host: " + m_serverName + ", with error " + e.getMessage());
	   } catch  (IOException e) {
		   writeGenericLog("No I/O " + e.getMessage());
	   }
	}
	
	protected void finalize(){
		try{
			m_client.close();
	    } catch (IOException e) {
	    	writeGenericLog("Could not close client socket " + m_client.toString() + ", with error = " + e.getMessage());
	    }
	}
}
