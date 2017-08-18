package common;

public class ErrorCode {
    public enum CrError {
        CR_OK,
        CR_UNEXPECTED,
        CR_NETWORK_ERROR,
        CR_QUEUE_MORE_WAITTIME,
        CR_EMPTY_QUEUE,
        CR_MALFORM_HTML,
        CR_INVALID_ARGS,
        CR_MALFORM_URL,
        CR_DATABASE_ERROR
    }

    public static boolean SUCCEEDED(CrError errorCode) {
        return errorCode == CrError.CR_OK;
    }

    public static boolean FAILED(CrError errorCode) {
        if (errorCode == null) {
            // TODO log null
            return false;
        }

        return errorCode != CrError.CR_OK;
    }
}
