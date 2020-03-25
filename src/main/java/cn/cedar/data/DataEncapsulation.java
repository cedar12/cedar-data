package cn.cedar.data;

import java.lang.reflect.Field;
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
            Set<Map.Entry<String,Object>> entrySet=map.entrySet();
            for(Map.Entry<String,Object> entry:entrySet){
                Field f= null;
                try {
                    f = cls.getDeclaredField(entry.getKey());
                    if(f!=null){
                        Object val=entry.getValue();
                        f.setAccessible(true);
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
                        f.set(obj,val);
                        f.setAccessible(false);
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

}
