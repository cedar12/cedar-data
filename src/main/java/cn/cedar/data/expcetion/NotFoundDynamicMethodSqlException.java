package cn.cedar.data.expcetion;

/**
 * @author cedar12.zxd@qq.com
 */
public class NotFoundDynamicMethodSqlException extends RuntimeException {
    public NotFoundDynamicMethodSqlException(String message) {
        super(message);
    }
}
