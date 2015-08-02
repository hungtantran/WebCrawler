package common;

public class ErrorCode {
	public enum CrError {
		CR_OK,
		CR_NETWORK_ERROR
	}
	
	public boolean succeed(CrError errorCode)
	{
		return errorCode == CrError.CR_OK;
	}
	
	public boolean failed(CrError errorCode)
	{
		return errorCode != CrError.CR_OK;
	}
}
