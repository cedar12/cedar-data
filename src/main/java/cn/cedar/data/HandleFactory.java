package cn.cedar.data;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 413338772@qq.com
 */
public final  class HandleFactory<T> {

    private static Map<Class,Object> proxyMap=new HashMap<>();

    private static JdbcManager jdbc=new JdbcManager();

    public static void setJdbc(JdbcManager jdbc){
        HandleFactory.jdbc=jdbc;
    }
    public static JdbcManager getJdbc(){
        return jdbc;
    }

    /**
     *
     * @param cls
     */
    public HandleFactory(Class<?>... cls) {
        for (Class<?> c : cls) {
            getInstance(c);
        }
    }

    /**
     * 获取接口实例
     * @param cls
     * @return
     */
    public T getInstance(Class<?> cls){
        Object proxyObj=proxyMap.get(cls);
        if(proxyObj==null){
            ProxyHandler proxyHandler=new ProxyHandler(cls,getMapperPath(cls),jdbc);
            proxyObj= Proxy.newProxyInstance(cls.getClassLoader(),new Class[]{cls},proxyHandler);
            proxyMap.put(cls,proxyObj);
        }
        return (T) proxyObj;
    }

    /**
     *
     * @param cls
     * @return
     */
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

