package cn.cedar.data;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyHandler implements InvocationHandler {

    public static Map<Method,String> sqlMap=new HashMap<>();

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
        Method[] methods=cls.getMethods();
        for(Method method:methods){

            String pattern = method.getName()+"\\s*?:\\s*?\\{(.*?)\\}\\s*?;";

            // 创建 Pattern 对象
            Pattern r = Pattern.compile(pattern,Pattern.DOTALL);

            // 现在创建 matcher 对象
            Matcher m = r.matcher(content);
            if (m.find( )) {
                sqlMap.put(method,m.group(1).trim());
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
            if (msg.charAt(i) == '{') {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    start = i;
                }
            } else if (msg.charAt(i) == '}') {
                endFlag++;
                if (endFlag == startFlag) {
                    Map<String,String> map=new HashMap<>();
                    map.put(msg.substring(start + 1, i),(start+1)+","+i);
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
                String[] index=e.getValue().split(",");
                String startSql=sql.substring(0,Integer.parseInt(index[0])-2);
                String endSql=sql.substring(Integer.parseInt(index[1])+1,sql.length());
                sql = startSql+eval+endSql;
            }
        }
        return sql;
    }

    public Object impl(Method method,Object[] args){
        Type t = method.getAnnotatedReturnType().getType();
        String regSql=sqlMap.get(method);
        Map<String,Object> paramsMap=args(method,args);
        String sql=parseSql(regSql,paramsMap);
        return exec(sql,t);
    }

    public Object exec(String sql,Type t){
        System.out.println(String.format("运行sql[%s]", sql));
        Object returnObj=null;
        int type=type(t);
        if(t==null||type<4){
            if(isDQL(sql)){
                returnObj=JdbcUtil.excuteQueryCount(sql);
                if(type<3){
                    returnObj=Integer.parseInt(returnObj.toString());
                }
            }else {
                returnObj = JdbcUtil.excute(sql);
            }
        }else{
            returnObj=JdbcUtil.excuteQuery(sql);
        }
        return returnObj;
    }

    public Object packDto(Type t,List<Map<String,Object>> mapList){

        return null;
    }

    public boolean isDQL(String sql){
        return sql.startsWith("select")||sql.startsWith("SELECT");
    }

    public int type(Type t){
        if(t==int.class){
            return 0;
        }
        if(t==Integer.class){
            return 1;
        }
        if(t==long.class){
            return 2;
        }
        if(t==Long.class){
            return 3;
        }
        return 4;
    }


    public Object eval(String reg,String var){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
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
