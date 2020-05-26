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

import cn.cedar.data.HandlerConstant;
import cn.cedar.data.expcetion.CannotResolveSymbolException;
import cn.cedar.data.expcetion.ConstParserException;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cedar12.zxd@qq.com
 */
public class DefStatementParser extends HandlerConstant {

    public static String parser(String content,Class<?> cls){
        Map<String,String> map=new LinkedHashMap<>();
        Matcher m=Pattern.compile(KEYWORD_DEF+"(\\s+?)((.+?)=(.+?));",Pattern.DOTALL|Pattern.MULTILINE).matcher(content);
        String tmp=content;
        while (m.find()){
            if(m.groupCount()==4){
                String value=m.group(2).trim().split("=")[1];
                if(!value.startsWith("{")){
                    if(map.get(m.group(3).trim())==null){
                        map.put(m.group(3).trim(),m.group(2));
                        int i=tmp.indexOf(m.group(0));
                        String beforeContent=tmp.substring(0,i);
                        String afterContent=tmp.substring(i+m.group(0).length());
                        if(beforeContent.trim().endsWith(KEYWORD_PRIVATE)){
                            beforeContent=beforeContent.trim().substring(0,beforeContent.trim().length()-7)+" ";
                        }
                        tmp=beforeContent+afterContent;
                    }
                }
            }
        }
        setMap.put(cls,map);
        return String.valueOf(eval(compile(tmp,map),map));
    }

    public static String compile(String content,Map<String,String> map){
        String tmp=content;
        Matcher m=Pattern.compile("#\\{(.+?)(\\})",Pattern.DOTALL|Pattern.MULTILINE).matcher(content);
        while (m.find()){
            if(m.groupCount()>0){
                String key=m.group(1).trim();
                if(key.contains(".")){
                    key=key.substring(0,key.indexOf("."));
                }else if(key.contains("[")&&key.contains("]")){
                    key=key.substring(0,key.indexOf("["));
                }
                if(map.containsKey(key)){
                    tmp=tmp.replaceAll("#\\{"+m.group(1).replaceAll("\\[","\\\\[").replaceAll("\\]","\\\\]").replaceAll("\\.","\\\\.")+"\\}","\"+"+m.group(1).trim()+"+\"");
                }else{
                    throw new CannotResolveSymbolException(key+"  Not Defined ");
                }
            }
        }
        return tmp;
    }

    private static Object eval(String content,Map<String,String> map){
        String var=EMPTY_SYMBOL;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(!var.equals("")){
                var+=";";
            }
            if(entry.getValue().split("=")[1].trim().startsWith("class:")){
                var += "var " + entry.getKey()+"=\""+entry.getValue().split("=")[1].trim()+"\"";
            }else {
                var += "var " + entry.getValue();
            }
        }
        var+=";";
        try {
            ENGINE.eval("function def_parse(){"+var+"return \""+content+"\"}");
        } catch (ScriptException e) {
            String msg=(e.getMessage().split("return")[1]).replaceAll("\"\\+","#{").replaceAll("\\+\"","}");
            msg=format(msg.trim().substring(1,msg.trim().length()-1));
            throw new ConstParserException("You have an error in  statement syntax"+"\n"+msg);
        }
        Invocable invocable = (Invocable) ENGINE;
        Object o = null;
        try {
            o = invocable.invokeFunction("def_parse");
        } catch (ScriptException e) {
            throw new ConstParserException("You have an error in  statement syntax");
        } catch (NoSuchMethodException e) {
        }
        return o;

    }

    public static String  format(String content){
        return content.replaceAll(":\\{",":{\r\n").replaceAll(";",";\r\n").replaceAll("};","\r\n};");
    }

}
