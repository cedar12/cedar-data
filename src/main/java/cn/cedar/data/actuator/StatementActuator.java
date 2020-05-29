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
package cn.cedar.data.actuator;

import cn.cedar.data.DataEncapsulation;
import cn.cedar.data.HandlerConstant;
import cn.cedar.data.InParams;
import cn.cedar.data.MapperData;
import cn.cedar.data.annotation.Param;
import cn.cedar.data.parser.CedarDataFileContentParser;
import cn.cedar.data.parser.CedarDataORMParser;
import cn.cedar.data.parser.ConditionParser;
import cn.cedar.data.struct.Block;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cedar12.zxd@qq.com
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
        String returnType=returnMap.get(method);
        if(isDQL(sql)){
            if(isNumber(cls)!=-1){
                data=performNumberDQL(cls,sql);
            }else if(InParams.isMap(cls)){
                data=jdbc.excuteQueryOne(sql);
            }else if(InParams.isList(cls)){
                List<Map<String,Object>> mapList=jdbc.excuteQuery(sql);
                if(InParams.isNull(returnType)||EMPTY_SYMBOL.equals(returnType)||MAP_SYMBOL.equalsIgnoreCase(returnType)||"java.util.Map".equals(returnType)){
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
                if(returnType!=null&&returnType.equalsIgnoreCase(KEY_SYMBOL)){
                    data=performNumberKeyDML(cls,sql);
                }else {
                    data = performNumberDML(cls, sql);
                }
            }else{
                jdbc.excute(sql);
            }
        }
        return data;
    }

    public static Object perform(Method method, Block block, CedarDataFileContentParser cedarData){
        Object data=null;
        Class<?> cls=method.getReturnType();
        String returnType=returnMap.get(method);
        String sql=block.getSql();
        if(isDQL(sql)){
            if(isNumber(cls)!=-1){
                data=performNumberDQL(cls,sql);
            }else if(InParams.isMap(cls)){
                data=execute(sql,SQL_TYPE_QUERY_ONE,block);
            }else if(InParams.isList(cls)){
                List<Map<String,Object>> mapList= (List<Map<String, Object>>) execute(sql,SQL_TYPE_QUERY,block);
                if(InParams.isNull(block.getType())||block.getType().isEmpty()||MAP_SYMBOL.equalsIgnoreCase(block.getType())||"java.util.Map".equals(block.getType())){
                    data=mapList;
                }else{
                    data=new CedarDataORMParser(mapList,cedarData).parse(block.getType());
                    data=new MapperData(mapList).parse(block.getType());
                    if(data==null){
                        List<Object> list=new ArrayList<>();
                        try {
                            Class<?> c=Class.forName(block.getType());
                            for (Map<String, Object> map : mapList) {
                                list.add(DataEncapsulation.encapsulation(c,map));
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        data=list;
                    }
                }
            }else{
                data= DataEncapsulation.encapsulation(cls, (Map<String, Object>) execute(sql,SQL_TYPE_QUERY_ONE,block));
            }
        }else{
            if(isNumber(cls)!=-1){
                if(returnType!=null&&returnType.equalsIgnoreCase(KEY_SYMBOL)){
                    data=performNumberKeyDML(cls,sql);
                }else {
                    data = performNumberDML(cls, sql);
                }
            }else{
                execute(sql,SQL_TYPE_UPDATE,block);
            }
        }
        return data;
    }

    private static Object execute(String sql,int type,Block block){
        Object[] args=block.getArgs();
        Object[] obj=new Object[0];
        if(args!=null){
            String[] names=new String[args.length];
            Method m=block.getMethod();

            Annotation[][] as=m.getParameterAnnotations();
            for (int i = 0; i < args.length; i++) {
                for (int i1=0;i1<as.length;i1++) {
                    Annotation[] a=as[i];
                    int index=-1;
                    for (int i2 = 0; i2 < a.length; i2++) {
                        if(a[i2] instanceof Param){
                            Param param=(Param)a[i2];
                            names[i]=param.value();
                            index=i2;
                            break;
                        }
                    }
                    if(index==-1){
                        names[i]=EMPTY_SYMBOL;
                    }
                }
            }

            Map<String,Object> map=new ConditionParser(names,args).parse(sql);
            sql=String.valueOf(map.get(ConditionParser.STR_SQL));
            List<Object> list= (List<Object>) map.get(ConditionParser.STR_ARGS);
            obj=new Object[list.size()];
            obj=list.toArray(obj);
        }
        if(displaySql&&getEnv().equals(ENV_CEDAR_DATA)){
            System.err.println(sql);
        }
        if(type==SQL_TYPE_QUERY){
            return jdbc.excuteQuery(sql,obj);
        }else if(type==SQL_TYPE_QUERY_ONE){
            return jdbc.excuteQueryOne(sql,obj);
        }else if(type==SQL_TYPE_UPDATE){
            return jdbc.excute(sql,obj);
        }
        return null;
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

    private static Object performNumberKeyDML(Class<?> o,String sql){
        Object result=null;
        int key=jdbc.excuteGetGeneratedKey(sql);
        if(InParams.isByte(o)){
            result=Byte.parseByte(String.valueOf(key));
        }else if(InParams.isShort(o)){
            result=Short.parseShort(String.valueOf(key));
        }else if(InParams.isInt(o)){
            result=key;
        }else if(InParams.isLong(o)){
            result=Long.parseLong(String.valueOf(key));
        }else if(InParams.isFloat(o)){
            result=Float.parseFloat(String.valueOf(key));
        }else if(InParams.isDouble(o)){
            result=Double.parseDouble(String.valueOf(key));
        }
        return result;
    }



    private static boolean isDQL(String sql){
        return sql.startsWith(HandlerConstant.SELECT_SYMBOL)||sql.startsWith(HandlerConstant.SELECT_SYMBOL.toUpperCase());
    }

}
