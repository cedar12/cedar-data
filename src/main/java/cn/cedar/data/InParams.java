package cn.cedar.data;

import cn.cedar.dto.TestDto;

import java.lang.reflect.Field;
import java.util.*;

public class InParams {
    
    public static boolean isByte(Object o){
        return o!=null&&(o instanceof Byte||o.getClass()==byte.class);
    }

    public static boolean isShort(Object o){
        return o!=null&&(o instanceof Short||o.getClass()==short.class);
    }

    public static boolean isInt(Object o){
        return o!=null&&(o instanceof Integer||o.getClass()==int.class);
    }

    public static boolean isLong(Object o){
        return o!=null&&(o instanceof Long||o.getClass()==long.class);
    }

    public static boolean isFloat(Object o){
        return o!=null&&(o instanceof Float||o.getClass()==float.class);
    }

    public static boolean isDouble(Object o){
        return o!=null&&(o instanceof Double||o.getClass()==double.class);
    }

    public static boolean isChar(Object o){
        return o!=null&&(o instanceof Character||o.getClass()==char.class);
    }

    public static boolean isString(Object o){
        return o!=null&&o instanceof String;
    }

    public static boolean isByteArray(Object o){
        return o!=null&o.getClass()==byte[].class;
    }

    public static boolean isLByteArray(Object o){
        return o!=null&&o instanceof Byte[];
    }

    public static boolean isShortArray(Object o){
        return o!=null&&o.getClass()==short[].class;
    }
    public static boolean isLShortArray(Object o){
        return o!=null&&o instanceof Short[];
    }

    public static boolean isIntArray(Object o){
        return o!=null&&o.getClass()==int[].class;
    }
    public static boolean isLIntArray(Object o){
        return o!=null&&o instanceof Integer[];
    }

    public static boolean isLongArray(Object o){
        return o!=null&&o.getClass()==long[].class;
    }
    public static boolean isLLongArray(Object o){
        return o!=null&&o instanceof Long[];
    }

    public static boolean isFloatArray(Object o){
        return o!=null&&o.getClass()==float[].class;
    }
    public static boolean isLFloatArray(Object o){
        return o!=null&&o instanceof Float[];
    }

    public static boolean isDoubleArray(Object o){
        return o!=null&&o.getClass()==double[].class;
    }
    public static boolean isLDoubleArray(Object o){
        return o!=null&&o instanceof Double[];
    }

    public static boolean isCharArray(Object o){
        return o!=null&&o.getClass()==char[].class;
    }
    public static boolean isLCharArray(Object o){
        return o!=null&&o instanceof Character[];
    }

    public static boolean isStringArray(Object o){
        return o!=null&&o instanceof String[];
    }

    public static boolean isList(Object o){
        return o!=null&&o instanceof List;
    }

    public static boolean isMap(Object o){
        return o!=null&&o instanceof Map;
    }

    public static boolean isNull(Object o){
        return o==null;
    }

    private static String stringPut(String[] tmps){
        String val=String.valueOf(HandleConstant.S_SYMBOL);
        for(int i=0;i<tmps.length;i++){
            if(tmps[i]==null){
                val+=tmps[i];
            }else {
                val += HandleConstant.SINGLE_SYMBOL + tmps[i] + HandleConstant.SINGLE_SYMBOL;
            }
            if(i<tmps.length-1){
                val+=HandleConstant.SPLIT_SYMBOL;
            }
        }
        val+=String.valueOf(HandleConstant.E_SYMBOL);
        return val;
    }

    private static String charPut(char[] tmps){
        String val=String.valueOf(HandleConstant.S_SYMBOL);
        for(int i=0;i<tmps.length;i++){
            val+=HandleConstant.SINGLE_SYMBOL+((char)tmps[i])+HandleConstant.SINGLE_SYMBOL;
            if(i<tmps.length-1){
                val+=HandleConstant.SPLIT_SYMBOL;
            }
        }
        val+=String.valueOf(HandleConstant.E_SYMBOL);
        return val;
    }


    protected static void in(Map<String,Object> paramsMap, String key, Object value,boolean preIs){
        if(isNull(key)){
            return;
        }
        if(isNull(value)){
            paramsMap.put(key, value);
        }else if (isString(value)||isChar(value)) {
            if(preIs){
                paramsMap.put(key, "\""+String.valueOf(HandleConstant.SINGLE_SYMBOL) + value +String.valueOf(HandleConstant.SINGLE_SYMBOL)+"\"");
            }else {
                paramsMap.put(key, String.valueOf(HandleConstant.SINGLE_SYMBOL) + value + String.valueOf(HandleConstant.SINGLE_SYMBOL));
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
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=HandleConstant.SINGLE_SYMBOL+tmps[i]+HandleConstant.SINGLE_SYMBOL;
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isByteArray(value)){
            byte[] tmps= (byte[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLByteArray(value)){
            Byte[] tmps= (Byte[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isShortArray(value)){
            short[] tmps= (short[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLShortArray(value)){
            Short[] tmps= (Short[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isIntArray(value)){
            int[] tmps= (int[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLIntArray(value)){
            Integer[] tmps= (Integer[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLongArray(value)){
            long[] tmps= (long[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLLongArray(value)){
            Long[] tmps= (Long[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isFloatArray(value)){
            float[] tmps= (float[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLFloatArray(value)){
            Float[] tmps= (Float[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isDoubleArray(value)){
            double[] tmps= (double[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isLDoubleArray(value)){
            Double[] tmps= (Double[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                val+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isList(value)){
            List tmps= (List) value;
            String val=HandleConstant.FLAG_SYMBOL+String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.size();i++){
                Map<String,Object> map=new HashMap<>();
                in(map,HandleConstant.KEY_SYMBOL,tmps.get(i),true);
                val+=map.get(HandleConstant.KEY_SYMBOL);
                if(i<tmps.size()-1){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(isMap(value)){
            Map tmp= (Map) value;
            String val=HandleConstant.FLAG_SYMBOL+String.valueOf(HandleConstant.START_SYMBOL);
            Set<Map.Entry> en=tmp.entrySet();
            boolean isFirst=true;
            for(Map.Entry e:en){
                if(!isFirst){
                    val+=HandleConstant.SPLIT_SYMBOL;
                }
                isFirst=false;
                Map<String,Object> rmap=new HashMap<>();
                in(rmap,HandleConstant.KEY_SYMBOL,e.getValue(),true);
                val+=String.valueOf(e.getKey())+HandleConstant.COLON_SYMBOL+rmap.get(HandleConstant.KEY_SYMBOL);
            }
            val+=String.valueOf(HandleConstant.END_SYMBOL);
            paramsMap.put(key, val);
        }else{
            Field[] fs=value.getClass().getDeclaredFields();
            String val=HandleConstant.FLAG_SYMBOL+String.valueOf(HandleConstant.START_SYMBOL);
            boolean isFirst=true;
            for (Field f : fs) {
                if(!isFirst){
                    val+=HandleConstant.SPLIT_SYMBOL;
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
                in(rmap,HandleConstant.KEY_SYMBOL,v,true);
                val+=f.getName()+HandleConstant.COLON_SYMBOL+rmap.get(HandleConstant.KEY_SYMBOL);

            }
            val+=String.valueOf(HandleConstant.END_SYMBOL);
            paramsMap.put(key, val);
        }

    }

}
