package cn.cedar.data;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyHandler implements InvocationHandler {

    private static Map<Method,String> sqlMap=new HashMap<>();
    private static Map<Method,String> returnMap=new HashMap<>();
    private static ScriptEngine engine = HandleConstant.MANAGER.getEngineByName(HandleConstant.JS_SYMBOL);

    private JdbcUtil jdbc;

    protected ProxyHandler(Class<?> cls,String path,JdbcUtil jdbc){
        this.jdbc=jdbc;
        URL url = ProxyHandler.class.getClassLoader().getResource("");
        File file = new File(url.getPath()+path);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String content="";
        try{
            //通过文件输入流获取文件通道流对象
            FileChannel inFc = new FileInputStream(file).getChannel();

            //读取数据
            buffer.clear();

            int length = inFc.read(buffer);
            content=new String(buffer.array(),0,length,"UTF-8");
            inFc.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        content = HandleConstant.ANNOTATION.matcher(content).replaceAll(HandleConstant.EMPTY_SYMBOL);
        HandleConstant.ANNOTATION = Pattern.compile("\r\n",Pattern.DOTALL);
        content = HandleConstant.ANNOTATION.matcher(content).replaceAll(HandleConstant.EMPTY_SYMBOL);
        Method[] methods=cls.getMethods();
        for(Method method:methods){
            String pattern = "\\s*?"+method.getName()+"((\\s+?(.*?)\\s*?)|\\s*?):\\s*?\\{((.*?))\\}\\s*?;";
            Pattern r = Pattern.compile(pattern,Pattern.DOTALL);
            Matcher m = r.matcher(content);
            if (m.find( )&&m.groupCount()>1) {
                String returnType=m.group(m.groupCount()-2);
                if(returnType==null){
                    returnType="";
                }
                returnMap.put(method,returnType.trim());
                sqlMap.put(method,m.group(m.groupCount()-1).trim());
            } else {
                System.out.println(method.getName()+" no match");
            }

        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            method.invoke(this,args);
        }else{
            return impl(method,args);
        }

        return null;
    }

    private static List<Map<String,String>> extractMessage(String msg) {
        List<Map<String,String>> list = new ArrayList<>();
        int start = 0;
        int startFlag = 0;
        int endFlag = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == HandleConstant.S_SYMBOL&&msg.charAt(i-1)==HandleConstant.WELL_SYMBOL) {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    start = i;
                }
            } else if (msg.charAt(i) == HandleConstant.E_SYMBOL) {
                endFlag++;
                if (endFlag == startFlag) {
                    Map<String,String> map=new HashMap<>();
                    map.put(msg.substring(start + 1, i),(start+1)+HandleConstant.SPLIT_SYMBOL+i);
                    list.add(map);
                }
            }
        }
        return list;
    }

    private Map<String, Object> args(Method method,Object[] args){
        Map<String,Object> paramsMap=new HashMap<>();
        if(args==null){
            return paramsMap;
        }
        Annotation[][] annos=method.getParameterAnnotations();
        List<Object> tmpArgs=new ArrayList<>();
        for (int i=0;i<args.length;i++){
            if(args[i] instanceof Map){
                Set<Map.Entry<String, Object>> entrySet = ((Map) args[i]).entrySet();
                for (Map.Entry<String, Object> entry : entrySet) {
                    handleArgs(paramsMap,entry.getKey().trim(),entry.getValue());
                }
            }else {
                tmpArgs.add(args[i]);
            }
        }
        for(int i=0;i<tmpArgs.size();i++){
            for (int j = 0; j < annos[i].length; j++) {
                Annotation anno = annos[i][j];
                if (anno != null && anno instanceof Param) {
                    Param param = (Param) anno;
                    if (tmpArgs.get(i)!= null && tmpArgs.get(i).getClass() == String.class) {
                        paramsMap.put(param.value().trim(), "'" + tmpArgs.get(i) + "'");
                    } else if (tmpArgs.get(i) != null && tmpArgs.get(i) instanceof Map) {
                        Set<Map.Entry<String, Object>> entrySet = ((Map) tmpArgs.get(i)).entrySet();
                        for (Map.Entry<String, Object> entry : entrySet) {
                            handleArgs(paramsMap,param.value().trim(),entry.getValue());
                        }
                    } else {
                        paramsMap.put(param.value().trim(), tmpArgs.get(i));
                    }
                }
            }
        }
        return  paramsMap;
    }

    private void handleArgs(Map<String,Object> paramsMap,String key,Object value){
        if(key==null){
            return;
        }
        if(value == null){
            paramsMap.put(key, value);
        }else if (value instanceof String||value instanceof Character||value.getClass()==char.class) {
            paramsMap.put(key, String.valueOf(HandleConstant.SINGLE_SYMBOL) + value +String.valueOf(HandleConstant.SINGLE_SYMBOL));
        }else if(value instanceof Number||value.getClass()==int.class||value.getClass()==long.class){
            paramsMap.put(key, value);
        } else if(value instanceof String[]){
            String[] tmps= (String[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                value+=HandleConstant.SINGLE_SYMBOL+tmps[i]+HandleConstant.SINGLE_SYMBOL;
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        } else if(value instanceof Number[]){
            Number[] tmps= (Number[]) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.length;i++){
                value+=String.valueOf(tmps[i]);
                if(i<tmps.length-1){
                    value+=",";
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        } else if(value instanceof List){
            List tmps= (List) value;
            String val=String.valueOf(HandleConstant.S_SYMBOL);
            for(int i=0;i<tmps.size();i++){
                if(value instanceof String||value instanceof Character||value.getClass()==char.class){
                    value+=String.valueOf(tmps.get(i));
                }else{
                    value+=String.valueOf(HandleConstant.SINGLE_SYMBOL)+String.valueOf(tmps.get(i))+String.valueOf(HandleConstant.SINGLE_SYMBOL);
                }
                if(i<tmps.size()-1){
                    value+=",";
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }else if(value instanceof Map){
            Map tmp= (Map) value;
            String val=String.valueOf(HandleConstant.START_SYMBOL);
            Set<Map.Entry> en=tmp.entrySet();
            boolean isFirst=true;
            for(Map.Entry e:en){
                if(!isFirst){
                    value+=",";
                }
                value+=String.valueOf(e.getKey())+e.getValue();
                if(e.getValue()!=null&&e.getValue() instanceof String||value instanceof Character||value.getClass()==char.class){
                    value+=String.valueOf(HandleConstant.SINGLE_SYMBOL)+e.getValue()+String.valueOf(HandleConstant.SINGLE_SYMBOL);
                }else{
                    value+=String.valueOf(e.getValue());
                }
            }
            val+=String.valueOf(HandleConstant.E_SYMBOL);
            paramsMap.put(key, val);
        }
    }

    private String parseSql(String regSql,Map<String,Object> paramsMap){

        List<Map<String, String>> reg=extractMessage(regSql);
        String sql=regSql.trim().replaceAll("\r",HandleConstant.EMPTY_SYMBOL).replaceAll("\n",HandleConstant.EMPTY_SYMBOL);
        for(int i=reg.size()-1;i>=0;i--) {
            Map<String,String> regs=reg.get(i);
            Set<Map.Entry<String, String>> entryMap=regs.entrySet();
            for(Map.Entry<String, String> e:entryMap){
                String var=HandleConstant.EMPTY_SYMBOL;
                Set<Map.Entry<String, Object>> entrySet = paramsMap.entrySet();
                for (Map.Entry<String, Object> entry : entrySet) {
                    if(entry.getValue()==null||entry instanceof Number){
                        var += "var " + entry.getKey() + "=" + entry.getValue() + ";";
                    }else {
                        var += "var " + entry.getKey() + "=\"" + entry.getValue() + "\";";
                    }
                }
                long time=System.currentTimeMillis();
                Object eval = eval(e.getKey(), var);
                String[] index=e.getValue().split(HandleConstant.SPLIT_SYMBOL);
                String startSql=sql.substring(0,Integer.parseInt(index[0])-2);
                String endSql=sql.substring(Integer.parseInt(index[1])+1,sql.length());
                sql = startSql+eval+endSql;
            }
        }
        return sql;
    }

    private Object impl(Method method,Object[] args){
        String regSql=sqlMap.get(method);
        if(regSql==null){
            return null;
        }
        Map<String,Object> paramsMap=args(method,args);
        String sql=parseSql(regSql,paramsMap);
        return exec(sql,method);
    }

    private Object exec(String sql,Method method){
        sql=sql.trim();
        System.out.println(String.format("sql[%s]", sql));
        Object returnObj=null;
        Class<?> t=method.getReturnType();
        int type=type(t);
        if(t==null||type<4){
            String returnType=returnMap.get(method);
            if(isDQL(sql)){
                returnObj=jdbc.excuteQueryCount(sql);
                returnObj=parseReturnValue(returnObj,type);
            }else if(returnType.equalsIgnoreCase(HandleConstant.KEY_SYMBOL)){
                returnObj=jdbc.excuteGetGeneratedKe(sql);
                returnObj=parseReturnValue(returnObj,type);
            }else{
                returnObj = jdbc.excute(sql);
                returnObj=parseReturnValue(returnObj,type);
            }
        }else{
            List<Map<String,Object>> listMap=jdbc.excuteQuery(sql);
            List<Object> returnList=packDto(method,JdbcUtil.formatHumpNameForList(listMap));
            if(returnList==null){
                returnObj=listMap;
            }else{
                returnObj=returnList;
            }
        }
        return returnObj;
    }

    private Object parseReturnValue(Object obj,int type){
        if(type==HandleConstant.TYPE_LONG||type==HandleConstant.TYPE_LONG_){
            obj=Long.parseLong(obj.toString());
        }if(type==HandleConstant.TYPE_INT||type==HandleConstant.TYPE_INTEGER){
            obj=Integer.parseInt(obj.toString());
        }
        return obj;
    }

    private List<Object> packDto(Method method,List<Map<String,Object>> mapList){
        Class<?> t=method.getReturnType();
        String returnType=returnMap.get(method);
        int type=type(t);
        if((!HandleConstant.EMPTY_SYMBOL.equals(returnType.trim()))&&(!HandleConstant.MAP_SYMBOL.equals(returnType))&&(!HandleConstant.PACK_MAP_SYMBOL.equals(returnType))&&type==4){
            List<Object> list=new ArrayList<>();
            try {
                Class<?> cls=Class.forName(returnType);
                for(int i=0;i<mapList.size();i++){
                    Object obj=cls.newInstance();
                    Map<String,Object> map=mapList.get(i);
                    Set<Map.Entry<String,Object>> entrySet=map.entrySet();
                    for(Map.Entry<String,Object> entry:entrySet){
                        Field f=cls.getDeclaredField(entry.getKey());
                        if(f!=null){
                            f.setAccessible(true);
                            f.set(obj,entry.getValue());
                            f.setAccessible(false);
                        }
                    }
                    list.add(obj);
                }
                return list;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean isDQL(String sql){
        return sql.startsWith(HandleConstant.SELECT_SYMBOL)||sql.startsWith(HandleConstant.SELECT_SYMBOL.toUpperCase());
    }

    private int type(Class<?> t){
        if(t==int.class){
            return HandleConstant.TYPE_INT;
        }
        if(t==Integer.class){
            return HandleConstant.TYPE_INTEGER;
        }
        if(t==long.class){
            return HandleConstant.TYPE_LONG;
        }
        if(t==Long.class){
            return HandleConstant.TYPE_LONG_;
        }
        return HandleConstant.TYPE_OTHER;
    }


    private Object eval(String reg,String var){
        String isReturnStr=reg.contains(HandleConstant.RETURN_SYMBOL)?HandleConstant.EMPTY_SYMBOL:HandleConstant.RETURN_SYMBOL;
        try {
            engine.eval(HandleConstant.FUN_SYMBOL+HandleConstant.ONE_EMPTY_SYMBOL + HandleConstant.EVAL_NAME_SYSMBOL + "()" +HandleConstant.START_SYMBOL+var+isReturnStr+HandleConstant.ONE_EMPTY_SYMBOL+reg+HandleConstant.END_SYMBOL);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) engine;
        Object o = null;
        try {
            o = invocable.invokeFunction(HandleConstant.EVAL_NAME_SYSMBOL);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return o;

    }

}
