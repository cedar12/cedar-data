package cn.cedar.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author cedar12.zxd@qq.com
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
                        Object val=caseValue(f,entry);
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

    private static Object caseValue(Field f,Map.Entry<String,Object> entry){
        Object val=entry.getValue();
        Class<?> type=f.getType();
        if(InParams.isString(type)) {
            if (InParams.isDate(val)) {
                val = ((Date) val);
                if (val != null) {
                    val = val.toString();
                }
            } else {
                val = String.valueOf(val);
            }
        }else if(InParams.isByte(type)){

        }else if(InParams.isInt(type)){

        }else if(InParams.isShort(type)){

        }else if(InParams.isLong(type)){

        }else if(InParams.isFloat(type)){

        }else if(InParams.isDouble(type)){

        }else if(InParams.isBigDecimal(type)){
            val=new BigDecimal(String.valueOf(val));
        }else if(InParams.isMap(type)){

        }else if(InParams.isList(type)){

        }

        return val;
    }


    private static void setValue(Object obj,Field f,Object val){
        Class<?> cls=obj.getClass();
        String methodName=getMethodName(f.getName());
        try {
            try {
                Method m = cls.getDeclaredMethod(methodName, f.getType());
                m.invoke(obj, val);
            } catch (NoSuchMethodException e) {
                f.setAccessible(true);
                f.set(obj, val);
                f.setAccessible(false);
            } catch (InvocationTargetException e) {
                f.setAccessible(true);
                f.set(obj, val);
                f.setAccessible(false);
            }
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }


    private static String getMethodName(String key){
        String name="set";
        return name+key.substring(0,1).toUpperCase()+key.substring(1);
    }

}
