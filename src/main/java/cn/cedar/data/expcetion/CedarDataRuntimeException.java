package cn.cedar.data.expcetion;

public class CedarDataRuntimeException extends RuntimeException {

    public CedarDataRuntimeException() {
        super();
    }

    public CedarDataRuntimeException(String message) {
        super(message);
    }

    public CedarDataRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CedarDataRuntimeException(Throwable cause) {
        super(cause);
    }

    protected CedarDataRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
