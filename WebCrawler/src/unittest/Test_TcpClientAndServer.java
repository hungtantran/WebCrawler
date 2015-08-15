package unittest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import static common.LogManager.writeGenericLog;
import static org.junit.Assert.*;

import common.ErrorCode.CrError;
import common.URLObject;
import proto.message.message.URLmessage;
import socket.IProcessPacket;
import socket.TcpClient;
import socket.TcpServer;

public class Test_TcpClientAndServer {
	private URLObject m_clientObject = null;
	private URLObject m_serverObject = null;
	
	private class ClientTestProcess implements IProcessPacket {
		@Override
		public CrError process(byte[] packet, DataOutputStream m_out) throws IOException {
			writeGenericLog("Client process packet");

			URLmessage urlMessage = URLmessage.parseFrom(packet);
			m_clientObject = URLObject.convertFromProtobufMessage(urlMessage);
			
			return null;
		}
	}
	
	private class ServerTestProcess implements IProcessPacket {
		@Override
		public CrError process(byte[] packet, DataOutputStream m_out) throws IOException {
			writeGenericLog("Server process packet");

			URLmessage urlMessage = URLmessage.parseFrom(packet);
			m_serverObject = URLObject.convertFromProtobufMessage(urlMessage);
			
			URLObject testUrl = new URLObject();
			testUrl.setLink("http://www.techcrunch.com");
			testUrl.set_priority(2);
			
			URLmessage message = testUrl.toProtobufMessage();
			byte[] messageByte = message.toByteArray();
			m_out.writeInt(messageByte.length);
			m_out.write(messageByte);
			
			return null;
		}
	}
	
	@Test
	public void test() {
		Test_ClientAndServer();
	}
	
	public void Test_ClientAndServer() {
		ExecutorService exec = Executors.newFixedThreadPool(2);

		TcpServer server = null;
		try {
			server = new TcpServer(8080, new ServerTestProcess());
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
		
		TcpClient client = null;
		try {
			client = new TcpClient("127.0.0.1", 8080, new ClientTestProcess());
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
		
		exec.execute(server);
		exec.execute(client);
		
		URLObject testUrl = new URLObject();
		testUrl.setLink("http://www.vnexpress.net");
		testUrl.set_priority(1);
		
		URLmessage message = testUrl.toProtobufMessage();
		client.sendPacket(message.toByteArray());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
		
		assertTrue(m_serverObject != null);
		assertEquals(m_serverObject.getLink(), "http://www.vnexpress.net");
		assertEquals(m_serverObject.get_priority(), 1);
		
		assertTrue(m_clientObject != null);
		assertEquals(m_clientObject.getLink(), "http://www.techcrunch.com");
		assertEquals(m_clientObject.get_priority(), 2);
	}
}
