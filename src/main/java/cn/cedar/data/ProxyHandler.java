package cn.cedar.data;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyHandler implements InvocationHandler {

    public static Map<Method,String> sqlMap=new HashMap<>();
    public static Map<Method,String> returnMap=new HashMap<>();

    public ProxyHandler(Class<?> cls,String path){
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
        content = HandleConstant.ANNOTATION.matcher(content).replaceAll("");
        HandleConstant.ANNOTATION = Pattern.compile("\r\n",Pattern.DOTALL);
        content = HandleConstant.ANNOTATION.matcher(content).replaceAll("");
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
                System.out.println(method.getName()+" NO MATCH");
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

    public static List<Map<String,String>> extractMessage(String msg) {
        List<Map<String,String>> list = new ArrayList<>();
        int start = 0;
        int startFlag = 0;
        int endFlag = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == HandleConstant.START_SYMBOL) {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    start = i;
                }
            } else if (msg.charAt(i) == HandleConstant.END_SYMBOL) {
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

    public Map<String, Object> args(Method method,Object[] args){
        Parameter[] params=method.getParameters();
        Map<String,Object> paramsMap=new HashMap<>();
        for(int i=0;i<params.length;i++){
            Annotation anno=params[i].getAnnotation(Param.class);
            if(anno!=null){
                Param param= (Param) anno;
                if(args[i]!=null&&args[i].getClass()==String.class){
                    paramsMap.put(param.value().trim(), "'"+args[i]+"'");
                }else {
                    paramsMap.put(param.value().trim(), args[i]);
                }
            }
        }
        return  paramsMap;
    }


    public String parseSql(String regSql,Map<String,Object> paramsMap){
        String var="";
        List<Map<String, String>> reg=extractMessage(regSql);
        String sql=regSql.trim().replaceAll("\r","").replaceAll("\n","");
        for(int i=reg.size()-1;i>=0;i--) {
            Map<String,String> regs=reg.get(i);
            Set<Map.Entry<String, String>> entryMap=regs.entrySet();
            for(Map.Entry<String, String> e:entryMap){
                Set<Map.Entry<String, Object>> entrySet = paramsMap.entrySet();
                for (Map.Entry<String, Object> entry : entrySet) {
                    if(entry.getValue()==null){
                        var += "var " + entry.getKey() + "=" + entry.getValue() + ";";
                    }else {
                        var += "var " + entry.getKey() + "=\"" + entry.getValue() + "\";";
                    }
                }
                Object eval = eval(e.getKey(), var);
                String[] index=e.getValue().split(HandleConstant.SPLIT_SYMBOL);
                String startSql=sql.substring(0,Integer.parseInt(index[0])-2);
                String endSql=sql.substring(Integer.parseInt(index[1])+1,sql.length());
                sql = startSql+eval+endSql;
            }
        }
        return sql;
    }

    public Object impl(Method method,Object[] args){
        String regSql=sqlMap.get(method);
        if(regSql==null){
            return null;
        }
        Map<String,Object> paramsMap=args(method,args);
        String sql=parseSql(regSql,paramsMap);
        return exec(sql,method);
    }

    public Object exec(String sql,Method method){
        sql=sql.trim();
        System.out.println(String.format("运行sql[%s]", sql));
        Object returnObj=null;
        Type t = method.getAnnotatedReturnType().getType();
        int type=type(t);
        if(t==null||type<4){
            if(isDQL(sql)){
                returnObj=JdbcUtil.excuteQueryCount(sql);
                if(type==HandleConstant.TYPE_INT||type==HandleConstant.TYPE_INTEGER){
                    returnObj=Integer.parseInt(returnObj.toString());
                }
            }else {
                returnObj = JdbcUtil.excute(sql);
                if(type==HandleConstant.TYPE_LONG||type==HandleConstant.TYPE_LONG_){
                    returnObj=Long.parseLong(returnObj.toString());
                }
            }
        }else{
            List<Map<String,Object>> listMap=JdbcUtil.excuteQuery(sql);
            List<Object> returnList=packDto(method,JdbcUtil.formatHumpNameForList(listMap));
            if(returnList==null){
                returnObj=listMap;
            }else{
                returnObj=returnList;
            }
        }
        return returnObj;
    }

    public List<Object> packDto(Method method,List<Map<String,Object>> mapList){
        Type t = method.getAnnotatedReturnType().getType();
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

    public boolean isDQL(String sql){
        return sql.startsWith(HandleConstant.SELECT_SYMBOL)||sql.startsWith(HandleConstant.SELECT_SYMBOL.toUpperCase());
    }

    public int type(Type t){
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


    public Object eval(String reg,String var){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(HandleConstant.JS_SYMBOL);
        try {
            engine.eval("function parse_express() {"+var+"return " + reg + ";}");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) engine;
        Object o = null;
        try {
            o = invocable.invokeFunction("parse_express");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return o;

    }

}
