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
     * Preloaded instance
     * @param cls
     */
    public static void preload(Class<?>... cls){
        for (Class<?> c : cls) {
            getInstance(c);
        }
    }

    /**
     * return instance
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
