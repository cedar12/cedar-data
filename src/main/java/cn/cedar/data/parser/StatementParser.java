package cn.cedar.data.parser;

import cn.cedar.data.HandlerConstant;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author 413338772@qq.com
 */
public class StatementParser extends HandlerConstant{

    private StatementParser(){};

    public static void parseContent(String content,Class<?> cls){
        content = ANNOTATION.matcher(content).replaceAll(EMPTY_SYMBOL);
        ANNOTATION = Pattern.compile("\r\n",Pattern.DOTALL);
        content = ANNOTATION.matcher(content).replaceAll(EMPTY_SYMBOL);
        Method[] methods=cls.getDeclaredMethods();
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

    public static void parseSql(Class<?> cls){
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
        parseSql(cls);
    }

}
