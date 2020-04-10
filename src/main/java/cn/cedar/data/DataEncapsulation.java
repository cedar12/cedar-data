package cn.cedar.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author 413338772@qq.com
 */
public class DataEncapsulation {

    /**
     *
     */
    private DataEncapsulation(){}

    /**
     *
     * @param cls 类
     * @param map 数据
     * @return 类实例
     */
    public static Object encapsulation(Class<?> cls, Map<String,Object> map){
        Object obj= null;
        try {
            obj = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(obj!=null){
            map=JdbcManager.formatHumpName(map);
            if(map==null||map.size()==0){return null;}
            Set<Map.Entry<String,Object>> entrySet=map.entrySet();
            for(Map.Entry<String,Object> entry:entrySet){
                Field f= null;
                try {
                    f = cls.getDeclaredField(entry.getKey());
                    if(f!=null){
                        Object val=entry.getValue();
                        if(f.getType()==String.class){
                            if(val instanceof Date){
                                val=((Date)val);
                                if(val!=null){
                                    val=val.toString();
                                }
                            }else {
                                val = String.valueOf(val);
                            }
                        }
                        String methodName=getMethodName(entry.getKey());
                        try {
                            Method m=cls.getDeclaredMethod(methodName,f.getType());
                            m.invoke(obj,val);
                        } catch (NoSuchMethodException e) {
                            f.setAccessible(true);
                            f.set(obj,val);
                            f.setAccessible(false);
                        } catch (InvocationTargetException e) {
                            f.setAccessible(true);
                            f.set(obj,val);
                            f.setAccessible(false);
                        }

                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
        return obj;
    }

    private static String getMethodName(String key){
        String name="set";
        return name+key.substring(0,1).toUpperCase()+key.substring(1);
    }

}
