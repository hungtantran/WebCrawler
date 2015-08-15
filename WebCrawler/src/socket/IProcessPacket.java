package socket;

import java.io.DataOutputStream;
import java.io.IOException;

import common.ErrorCode.CrError;

public interface IProcessPacket {
	public CrError process(byte[] packet, DataOutputStream m_out) throws IOException;
}
