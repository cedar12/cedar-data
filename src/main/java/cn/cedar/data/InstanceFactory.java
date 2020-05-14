package cn.cedar.data;

import java.lang.reflect.Proxy;

/**
 * @author cedar12.zxd@qq.com
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
     * 预加载实例
     * @param cls
     */
    public static void preload(Class<?>... cls){
        for (Class<?> c : cls) {
            getInstance(c);
        }
    }

    /**
     * 获取实例
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
