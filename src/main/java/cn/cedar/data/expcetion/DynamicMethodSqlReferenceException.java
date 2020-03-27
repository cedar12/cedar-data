package cn.cedar.data.expcetion;

import java.lang.reflect.Method;

/**
 * 表达式解析异常
 * @author 413338772@qq.com
 */
public class DynamicMethodSqlReferenceException extends Exception{
    public DynamicMethodSqlReferenceException(String message) {
        super(message);
    }
    public DynamicMethodSqlReferenceException(Method method, String message) {
        super(method.getDeclaringClass()+"   Method: "+method.getName()+"   "+message);
    }
}
