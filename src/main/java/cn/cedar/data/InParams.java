package cn.cedar.data;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author 413338772@qq.com
 */
public class InParams extends HandlerConstant{

    /**
     *
     * @param o
     * @return
     */
    public static boolean isByte(Object o){
        return o!=null&&(o instanceof Byte||o.getClass()==byte.class);
    }

    /**
     *
     * @param o
     * @return
     */
    public static boolean isByte(Class<?> o){
        return o!=null&&(o==Byte.class||o==byte.class);
    }

    /**
     *
     * @param o
     * @return
     */
    public static boolean isShort(Object o){
        return o!=null&&(o instanceof Short||o.getClass()==short.class);
    }

    /**
     *
     * @param o
     * @return
     */
    public static boolean isShort(Class<?> o){
        return o!=null&&(o==Short.class||o==short.class);
    }

    /**
     *
     * @param o
     * @return
     */
    public static boolean isInt(Object o){
        return o!=null&&(o instanceof Integer||o.getClass()==int.class);
    }

    /**
     *
     * @param o
     * @return
     */
    public static boolean isInt(Class<?> o){
        return o!=null&&(o==Integer.class||o==int.class);
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isLong(Object o){
        return o!=null&&(o instanceof Long||o.getClass()==long.class);
    }

    /**
     *
     * @param o
     * @return
     */
    public static boolean isLong(Class<?> o){
        return o!=null&&(o==Long.class||o==long.class);
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isFloat(Object o){
        return o!=null&&(o instanceof Float||o.getClass()==float.class);
    }

    /**
     *
     * @param o
     * @return
     */
    public static boolean isFloat(Class<?> o){
        return o!=null&&(o == Float.class||o==float.class);
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isDouble(Object o){
        return o!=null&&(o instanceof Double||o.getClass()==double.class);
    }

    /**
     *
     * @param o
     * @return
     */
    public static boolean isDouble(Class<?> o){
        return o!=null&&(o== Double.class||o==double.class);
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isChar(Object o){
        return o!=null&&(o instanceof Character||o.getClass()==char.class);
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isString(Object o){
        return o!=null&&o instanceof String;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isByteArray(Object o){
        return o!=null&o.getClass()==byte[].class;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isLByteArray(Object o){
        return o!=null&&o instanceof Byte[];
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isShortArray(Object o){
        return o!=null&&o.getClass()==short[].class;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isLShortArray(Object o){
        return o!=null&&o instanceof Short[];
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isIntArray(Object o){
        return o!=null&&o.getClass()==int[].class;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isLIntArray(Object o){
        return o!=null&&o instanceof Integer[];
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isLongArray(Object o){
        return o!=null&&o.getClass()==long[].class;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isLLongArray(Object o){
        return o!=null&&o instanceof Long[];
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isFloatArray(Object o){
        return o!=null&&o.getClass()==float[].class;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isLFloatArray(Object o){
        return o!=null&&o instanceof Float[];
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isDoubleArray(Object o){
        return o!=null&&o.getClass()==double[].class;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isLDoubleArray(Object o){
        return o!=null&&o instanceof Double[];
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isCharArray(Object o){
        return o!=null&&o.getClass()==char[].class;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isLCharArray(Object o){
        return o!=null&&o instanceof Character[];
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isStringArray(Object o){
        return o!=null&&o instanceof String[];
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isList(Object o){
        return o!=null&&o instanceof List;
    }
    public static boolean isList(Class<?> o){
        return o!=null&&o== List.class;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isMap(Object o){
        return o!=null&&o instanceof Map;
    }
    public static boolean isMap(Class<?> o){
        return o!=null&&o== Map.class;
    }

    public static boolean isDate(Object o){
        return o!=null&&o instanceof Date;
    }
    public static boolean isDate(Class<?> o){
        return o!=null&&o== Date.class;
    }
    /**
     *
     * @param o
     * @return
     */
    public static boolean isNull(Object o){
        return o==null;
    }

    /**
     *
     * @param tmps
     * @return
     */
    private static String stringPut(String[] tmps){
        String val=String.valueOf(HandlerConstant.S_SYMBOL);
        for(int i=0;i<tmps.length;i++){
            if(tmps[i]==null){
                val+=tmps[i];
            }else {
                val += HandlerConstant.SINGLE_SYMBOL + tmps[i] + HandlerConstant.SINGLE_SYMBOL;
            }
            if(i<tmps.length-1){
                val+= HandlerConstant.SPLIT_SYMBOL;
            }
        }
        val+=String.valueOf(HandlerConstant.E_SYMBOL);
        return val;
    }

    /**
     *
     * @param tmps
     * @return
     */
    private static String charPut(char[] tmps){
        String val=String.valueOf(HandlerConstant.S_SYMBOL);
        for(int i=0;i<tmps.length;i++){
            val+= HandlerConstant.SINGLE_SYMBOL+((char)tmps[i])+ HandlerConstant.SINGLE_SYMBOL;
            if(i<tmps.length-1){
                val+= HandlerConstant.SPLIT_SYMBOL;
            }
        }
        val+=String.valueOf(HandlerConstant.E_SYMBOL);
        return val;
    }

    /**
     *
     * @param paramsMap
     * @param key
     * @param value
     * @param preIs
     */
    protected static void in(Map<String,Object> paramsMap, String key, Object value,boolean preIs){
        if(isNull(key)){
            return;
        }
        if(isNull(value)){
            paramsMap.put(key, value);
        }else if (isString(value)||isChar(value)) {
            String val=value.toString().replaceAll("-- ","").replaceAll("//","").replaceAll("/*","").replaceAll("\\*\\/","");
            if(preIs){
                paramsMap.put(key, "\""+String.valueOf(HandlerConstant.SINGLE_SYMBOL) + val +String.valueOf(HandlerConstant.SINGLE_SYMBOL)+"\"");
            }else {
                paramsMap.put(key, String.valueOf(HandlerConstant.SINGLE_SYMBOL) + val + String.valueOf(HandlerConstant.SINGLE_SYMBOL));
            }
        }else if(isByte(value)||isShort(value)||isInt(value)||isLong(value)||isFloat(value)||isDouble(value)){
            paramsMap.put(key, value);
        }else if(isStringArray(value)){
            String[] tmps= (String[]) value;
            paramsMap.put(key, stringPut(tmps));
        }else if(isCharArray(value)){
            char[] tmps= (char[]) value;
            paramsMap.put(key, charPut(tmps));
        }else if(isLCharArray(value)){
            Character[] tmps= (Character[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+= HandlerConstant.SINGLE_SYMBOL+tmps[i]+ HandlerConstant.SINGLE_SYMBOL;
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isByteArray(value)){
            byte[] tmps= (byte[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLByteArray(value)){
            Byte[] tmps= (Byte[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isShortArray(value)){
            short[] tmps= (short[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLShortArray(value)){
            Short[] tmps= (Short[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isIntArray(value)){
            int[] tmps= (int[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLIntArray(value)){
            Integer[] tmps= (Integer[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLongArray(value)){
            long[] tmps= (long[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLLongArray(value)){
            Long[] tmps= (Long[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isFloatArray(value)){
            float[] tmps= (float[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLFloatArray(value)){
            Float[] tmps= (Float[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isDoubleArray(value)){
            double[] tmps= (double[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLDoubleArray(value)){
            Double[] tmps= (Double[]) value;
            String val=String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(value instanceof Object[]||value.getClass()==Object[].class){
            Object[] tmps= (Object[]) value;
            String val= HandlerConstant.FLAG_SYMBOL+String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                Map<String,Object> map=new HashMap<>();
                in(map, HandlerConstant.KEY_SYMBOL,tmps[i],true);
                val+=map.get(HandlerConstant.KEY_SYMBOL);
                if(i<tmps.length-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isList(value)){
            List tmps= (List) value;
            String val= HandlerConstant.FLAG_SYMBOL+String.valueOf(HandlerConstant.S_SYMBOL);
            for(int i=0;i<tmps.size();i++){
                Map<String,Object> map=new HashMap<>();
                in(map, HandlerConstant.KEY_SYMBOL,tmps.get(i),true);
                val+=map.get(HandlerConstant.KEY_SYMBOL);
                if(i<tmps.size()-1){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandlerConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isMap(value)){
            Map tmp= (Map) value;
            String val= HandlerConstant.FLAG_SYMBOL+String.valueOf(HandlerConstant.START_SYMBOL);
            Set<Map.Entry> en=tmp.entrySet();
            boolean isFirst=true;
            for(Map.Entry e:en){
                if(!isFirst){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
                isFirst=false;
                Map<String,Object> rmap=new HashMap<>();
                in(rmap, HandlerConstant.KEY_SYMBOL,e.getValue(),true);
                val+=String.valueOf(e.getKey())+ HandlerConstant.COLON_SYMBOL+rmap.get(HandlerConstant.KEY_SYMBOL);
            }
            val+=String.valueOf(HandlerConstant.END_SYMBOL);
            paramsMap.put(key, val);
        }else if(isDate(value)){
            Date date= (Date) value;
            String val="new Date("+date.getTime()+")";
            paramsMap.put(key, val);
        }else{
            Field[] fs=value.getClass().getDeclaredFields();
            String val= HandlerConstant.FLAG_SYMBOL+String.valueOf(HandlerConstant.START_SYMBOL);
            boolean isFirst=true;
            for (Field f : fs) {
                if(!isFirst){
                    val+= HandlerConstant.SPLIT_SYMBOL;
                }
                isFirst=false;
                Object v=null;
                f.setAccessible(true);
                try {
                    v=f.get(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                f.setAccessible(false);
                Map<String,Object> rmap=new HashMap<>();
                in(rmap, HandlerConstant.KEY_SYMBOL,v,true);
                val+=f.getName()+ HandlerConstant.COLON_SYMBOL+rmap.get(HandlerConstant.KEY_SYMBOL);

            }
            val+=String.valueOf(HandlerConstant.END_SYMBOL);
            paramsMap.put(key, val);
        }

    }

}
