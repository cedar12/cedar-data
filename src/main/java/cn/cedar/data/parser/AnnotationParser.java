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
package cn.cedar.data.parser;

import cn.cedar.data.annotation.Def;
import cn.cedar.data.annotation.Query;
import cn.cedar.data.expcetion.ReferenceException;
import cn.cedar.data.expcetion.SyntaxException;
import cn.cedar.data.struct.Block;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cedar12.zxd@qq.com
 */
public class AnnotationParser extends CedarDataFileContentParser {

    private Class<?> target;

    public AnnotationParser(Class<?> target){
        this.target=target;
        this.parse();
    }

    public void parse(){
        defParse();
        Method[] methods=target.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations=method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof Query){
                    query((Query) annotation,method);
                }
            }
        }
        super.parse(target);
    }

    public void defParse(){
        List<Map<String,Object>> list=new ArrayList<>();
        Method[] methods=target.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations=method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof Def){
                    Map map=new HashMap();
                    map.put(KEYWORD_DEF,annotation);
                    map.put(KEYWORD_TARGET,method);
                    list.add(map);
                }
            }
        }
        Field[] fields=target.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations=field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof Def){
                    Map map=new HashMap();
                    map.put(KEYWORD_DEF,annotation);
                    map.put(KEYWORD_TARGET,field);
                    list.add(map);
                }
            }
        }
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Def def1= (Def) o1.get(KEYWORD_DEF);
                Def def2= (Def) o2.get(KEYWORD_DEF);
                return def2.order()-def1.order();
            }
        });
        for (Map<String, Object> map : list) {
            Object annotation=map.get(KEYWORD_DEF);
            Object target=map.get(KEYWORD_TARGET);
            if(target instanceof Method){
                def((Def) annotation,(Method)target);
            }else if(target instanceof Field){
                def((Def) annotation,(Field)target);
            }
        }

    }

    private String defRelolver(String line,Object target) {
        String newLine=line;
        Pattern p=Pattern.compile(PATTERN_RELOLVER);
        Matcher m=p.matcher(line);
        while(m.find()) {
            for (int i = 0; i < m.groupCount(); i++) {
                String var=m.group(i);
                String key=var.substring(2, var.length()-1).trim();
                String value=defs.get(key);
                if(value==null) {
                    throw new ReferenceException(String.format("%s %s:%s not found", target,line,var));
                }else {
                    newLine=newLine.replace(var, value);
                }
            }
        }
        return newLine;
    }

    public void query(Query dql, Method method){
        String sql=dql.value();
        Class<?> returnType=dql.typeClass();
        String type=dql.type();
        if(type.isEmpty()){
            type=returnType.getName();
        }
        type=defRelolver(type,method);
        sql=defRelolver(sql,method);
        Block block=new Block();
        block.setTarget(target);
        block.setBody(sql);
        block.setName(method.getName());
        block.setType(type);
        blocks.add(block);
    }

    public void def(Def def,Object target){
        Method method=null;
        Field field=null;
        if(target instanceof Method){
            method= (Method) target;
        }else if(target instanceof Field){
            field= (Field) target;
        }
        String name=def.name();
        String value=def.value();
        if(name.isEmpty()){
            if(method==null){
                name=field.getName();
            }else if(field==null){
                name=method.getName();
            }
        }
        if(value.isEmpty()){
            throw new SyntaxException(String.format("%s @Def value is empty!", target));
        }
        value=defRelolver(value,target);
        defs.put(name,value);
    }

    public static Object getObject(Class<?> cls){
        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean test(String test,Object[] params){

        Pattern pattern=Pattern.compile(":(\\d+?)");
        Matcher matcher=pattern.matcher(test);
        while (matcher.find()){
            for (int i = 0; i < matcher.groupCount(); i++) {
                System.out.println(matcher.group(i));
                test=test.replace(matcher.group(i),"arg"+matcher.group(i).substring(1));
            }
        }
        System.out.println(test);

        ScriptEngineManager manager=new ScriptEngineManager();
        ScriptEngine engine=manager.getEngineByName("js");

        for (int i = 0; i < params.length; i++) {
            engine.put("arg"+(i+1),params[i]);
        }

        try {
            Object result=engine.eval(test);
            if(result instanceof Boolean){
                return (Boolean)result;
            }else{
                System.out.println("test error");
            }

        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return false;
    }
}
