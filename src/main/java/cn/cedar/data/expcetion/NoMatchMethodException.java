package cn.cedar.data.expcetion;

import java.lang.reflect.Method;

/**
 * 未找到method对应文件的sql异常
 */
public class NoMatchMethodException extends Exception {
    public NoMatchMethodException(Method method) {
        super("not match method \n%s\n"+method);
    }
}
