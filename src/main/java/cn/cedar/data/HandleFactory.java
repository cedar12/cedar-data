package cn.cedar.data;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


public final  class HandleFactory<T> {

    private static Map<Class,Object> proxyMap=new HashMap<>();

    public HandleFactory(Class<?>... cls) {
        for (Class<?> c : cls) {
            getInstance(c);
        }
    }

    public T getInstance(Class<?> cls){
        Object proxyObj=proxyMap.get(cls);
        if(proxyObj==null){
            ProxyHandler proxyHandler=new ProxyHandler(cls,getMapperPath(cls));
            proxyObj= Proxy.newProxyInstance(cls.getClassLoader(),new Class[]{cls},proxyHandler);
            proxyMap.put(cls,proxyObj);
        }
        return (T) proxyObj;
    }

    private static String getMapperPath(Class<?> cls){
        String[] paths=cls.getName().split("\\.");
        String path="";
        for (int i=0;i<paths.length;i++){
            path+=paths[i];
            if(i<paths.length-1){
                path+="\\"+File.separator;
            }
        }
        return path;
    }

}
