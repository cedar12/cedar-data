package cn.cedar.data.expcetion;

public class NotFoundDynamicMethodSqlException extends RuntimeException {
    public NotFoundDynamicMethodSqlException(String message) {
        super(message);
    }
}
