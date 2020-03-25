package cn.cedar.data.actuator;

import cn.cedar.data.DataEncapsulation;
import cn.cedar.data.HandlerConstant;
import cn.cedar.data.InParams;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 413338772@qq.com
 */
public class StatementActuator extends HandlerConstant {

    private StatementActuator(){}

    public static int  isNumber(Class<?> c){
        if(InParams.isByte(c)){
            return 0;
        }
        if(InParams.isShort(c)){
            return 1;
        }
        if(InParams.isInt(c)){
            return 2;
        }
        if(InParams.isLong(c)){
            return 3;
        }
        if(InParams.isFloat(c)){
            return 4;
        }
        if(InParams.isDouble(c)){
            return 5;
        }
        return -1;
    }

    public static Object perform(Method method, String sql){
        Object data=null;
        Class<?> cls=method.getReturnType();
        if(isDQL(sql)){
            if(isNumber(cls)!=-1){
                data=performNumberDQL(cls,sql);
            }else if(InParams.isMap(cls)){
                data=jdbc.excuteQueryOne(sql);
            }else if(InParams.isList(cls)){
                List<Map<String,Object>> mapList=jdbc.excuteQuery(sql);
                String returnType=returnMap.get(method);
                if(InParams.isNull(returnType)||EMPTY_SYMBOL.equals(returnType)){
                    data=mapList;
                }else{
                    List<Object> list=new ArrayList<>();
                    try {
                        Class<?> c=Class.forName(returnType);
                        for (Map<String, Object> map : mapList) {
                            list.add(DataEncapsulation.encapsulation(c,map));
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    data=list;
                }
            }else{
                data= DataEncapsulation.encapsulation(cls,jdbc.excuteQueryOne(sql));
            }
        }else{
            if(isNumber(cls)!=-1){
                data=performNumberDML(cls,sql);
            }else{
                jdbc.excute(sql);
            }
        }
        return data;
    }


    private static Object performNumberDQL(Class<?> o,String sql){
        Object result=null;
        long count=jdbc.excuteQueryCount(sql);
        if(InParams.isByte(o)){
            result=Byte.parseByte(String.valueOf(count));
        }else if(InParams.isShort(o)){
            result=Short.parseShort(String.valueOf(count));
        }else if(InParams.isInt(o)){
            result=Integer.parseInt(String.valueOf(count));
        }else if(InParams.isLong(o)){
            result=count;
        }else if(InParams.isFloat(o)){
            result=Float.parseFloat(String.valueOf(count));
        }else if(InParams.isDouble(o)){
            result=Double.parseDouble(String.valueOf(count));
        }
        return result;
    }
    private static Object performNumberDML(Class<?> o,String sql){
        Object result=null;
        int count=jdbc.excute(sql);
        if(InParams.isByte(o)){
            result=Byte.parseByte(String.valueOf(count));
        }else if(InParams.isShort(o)){
            result=Short.parseShort(String.valueOf(count));
        }else if(InParams.isInt(o)){
            result=count;
        }else if(InParams.isLong(o)){
            result=Long.parseLong(String.valueOf(count));
        }else if(InParams.isFloat(o)){
            result=Float.parseFloat(String.valueOf(count));
        }else if(InParams.isDouble(o)){
            result=Double.parseDouble(String.valueOf(count));
        }
        return result;
    }



    private static boolean isDQL(String sql){
        return sql.startsWith(HandlerConstant.SELECT_SYMBOL)||sql.startsWith(HandlerConstant.SELECT_SYMBOL.toUpperCase());
    }

}
