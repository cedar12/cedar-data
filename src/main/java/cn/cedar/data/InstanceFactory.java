package cn.cedar.data;

import java.lang.reflect.Proxy;

/**
 * @author 413338772@qq.com
 */
public class InstanceFactory extends HandlerConstant{

    /**
     *
     */
    private InstanceFactory(){}

    /**
     *
     * @param cls
     */
    public InstanceFactory(Class<?>... cls){
        for (Class<?> c : cls) {
            getInstance(c);
        }
    }

    /**
     *
     * @param cls
     * @param <T>
     * @return
     */
    public static <T>  T getInstance(Class<?> cls){
        Object proxyInstance=proxyMap.get(cls);
        if(proxyInstance==null){
            InstanceProxy proxy=new InstanceProxy(cls);
            proxyInstance= Proxy.newProxyInstance(cls.getClassLoader(),new Class[]{cls},proxy);
            proxyMap.put(cls,proxyInstance);
        }
        return (T) proxyInstance;
    }

}
