/**
 *	  Copyright 2020 cedar12.zxd@qq.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.cedar.data;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @see cn.cedar.data.InstanceFactory
 * @author cedar12.zxd@qq.com
 */
public final class HandlerFactory<T> {

    private static Map<Class,Object> proxyMap=new HashMap<>();

    private static JdbcManager jdbc=new JdbcManager();

    public static void setJdbc(JdbcManager jdbc){
        HandlerFactory.jdbc=jdbc;
    }
    public static JdbcManager getJdbc(){
        return jdbc;
    }

    /**
     *@see cn.cedar.data.InstanceFactory
     */
    @Deprecated
    public HandlerFactory(){}

    /**
     *@see cn.cedar.data.InstanceFactory
     * @param cls
     */
    @Deprecated
    public HandlerFactory(Class<?>... cls) {
        for (Class<?> c : cls) {
            getInstance(c);
        }
    }

    /**
     * get an interface instance
     * @param cls
     * @return
     */
    @Deprecated
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

