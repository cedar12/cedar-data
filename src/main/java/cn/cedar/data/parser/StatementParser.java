package cn.cedar.data.parser;

import cn.cedar.data.HandlerConstant;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author 413338772@qq.com
 */
public class StatementParser extends HandlerConstant{

    private StatementParser(){};

    private static void parseContent(String content,Class<?> cls){
        content = content.replaceAll("\r|\n", "");
        StringTokenizer pas = new StringTokenizer(content, " ");
        content = "";
        while (pas.hasMoreTokens()) {
            String s = pas.nextToken();
            content = content + s + " ";
        }
        content = ANNOTATION.matcher(content).replaceAll(EMPTY_SYMBOL);

        Method[] methods=cls.getDeclaredMethods();
        Pattern p=Pattern.compile("\\s+?");
        for(Method method:methods){
            String pattern ="\\s*?"+method.getName()+"((\\s+?(.*?)\\s*?)|\\s*?):\\s*?\\{((.*?))\\}\\s*?;";
            Pattern r = Pattern.compile(pattern,Pattern.DOTALL);
            Matcher m = r.matcher(content);
            if (m.find( )&&m.groupCount()>1) {
                String returnType=m.group(m.groupCount()-2);
                if(returnType==null){
                    returnType="";
                }
                returnMap.put(method,returnType.trim());
                String sql=m.group(m.groupCount()-1).trim();
                sqlMap.put(method,sql);
            }else{
                System.err.println(method.getName()+" no match");
            }
        }
    }

    private static void parseSql(Class<?> cls){
        Method[] methods=cls.getDeclaredMethods();
        for(Method method:methods) {
            String statement=sqlMap.get(method);
            String sql=statement;
            Pattern p=Pattern.compile("#\\[(.+?)\\]",Pattern.DOTALL|Pattern.MULTILINE);
            Matcher m=p.matcher(sql);
            List<String> expressList=new ArrayList<>();
            int count=0;
            while(m.find()){
                expressList.add(m.group(0));
                sql=sql.replace(m.group(0),placeholderSymbol(count));
                count++;
            }
            Map<String,Object> map=new HashMap<>();
            map.put(SQL_SYMBOL,sql);
            map.put(EXP_SYMBOL,expressList);
            parseSqlMap.put(method,map);
        }
    }

    public static void parse(String content,Class<?> cls){
        parseContent(content,cls);
        Method[] methods=cls.getDeclaredMethods();
        for(Method method:methods) {
            String sql=sqlMap.get(method);
            List<String> list=new ArrayList<>();
            parseSql(sql,list);
            Map<String,Object> map=new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                String line=list.get(i);
                String[] indexs=line.substring(line.lastIndexOf(",")+1).split(COLON_SYMBOL);
                sql=sql.substring(0,Integer.parseInt(indexs[0])-1)+placeholderSymbol(i)+sql.substring(Integer.parseInt(indexs[1])+1);
            }
            map.put(EXP_SYMBOL,list);
            map.put(SQL_SYMBOL,sql);
            parseSqlMap.put(method,map);
        }
    }

    private static void parseSql(String msg,List<String> list){
        if(msg==null||msg.isEmpty()){return;}
        char[] chars=msg.toCharArray();
        int s=-1,e=0;
        s=msg.lastIndexOf(S_SYMBOL);
        if(s<=-1){
            return;
        }
        for(int i=s;i<chars.length;i++){
            if(chars[i]==E_SYMBOL){
                e=i;
                break;
            }
        }
        chars[s]=S_TMP_SYMBOL;
        chars[e]=E_TMP_SYMBOL;
        if(s>0&&chars[s-1]==EXP_FLAG_SYMBOL){
            list.add((msg.substring(s,e+1).replaceAll(String.valueOf(S_TMP_SYMBOL),String.valueOf(S_SYMBOL)).replaceAll(String.valueOf(E_TMP_SYMBOL),String.valueOf(E_SYMBOL)))+SPLIT_SYMBOL+s+COLON_SYMBOL+e);
        }
        parseSql(new String(chars),list);
    }

}
