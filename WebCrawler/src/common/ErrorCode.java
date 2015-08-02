package common;

public class ErrorCode {
	public enum CrError {
		CR_OK,
		CR_NETWORK_ERROR
	}
	
	public static boolean SUCCEEDED(CrError errorCode)
	{
		return errorCode == CrError.CR_OK;
	}
	
	public static boolean FAILED(CrError errorCode)
	{
		if (errorCode == null)
		{
			// TODO log null
			return false;
		}

		return errorCode != CrError.CR_OK;
	}
}
