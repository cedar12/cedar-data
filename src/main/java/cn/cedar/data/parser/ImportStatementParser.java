package cn.cedar.data.parser;

import cn.cedar.data.HandlerConstant;
import cn.cedar.data.expcetion.ImportLevelExcpetion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportStatementParser extends HandlerConstant{

    public static String importParser(String content,Class<?> cls,int layer) {
        Matcher m=Pattern.compile("import(\\s+?)(.+?);", Pattern.DOTALL|Pattern.MULTILINE).matcher(content);
        List<String> imports=new ArrayList<>();
        String tmp=content;
        while(m.find()) {
            if(m.groupCount()>1) {
                tmp=tmp.replace(m.group(0), "");
                imports.add(m.group(0).replace("import", "").replace(";", "").trim());
            }
        }
        layer++;
        if(layer<=MAX_LAYER) {
            for (String path : imports) {
                String t=importParser(load(path, cls), cls, layer);
                if(!tmp.contains(t)){
                    tmp+=t;
                }
            }
        }else{
            throw new ImportLevelExcpetion("Too many import levels, no more than "+MAX_LAYER);
        }
        return tmp;
    }

    private static String load(String path,Class<?> cls){
        if(!path.startsWith(FILE_SPLIT_SYMBOL)){
            path = FILE_SPLIT_SYMBOL + path;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(cls.getResourceAsStream(path)));
        StringBuffer buffer = new StringBuffer();
        String line = EMPTY_SYMBOL;
        while (true){
            try {
                if (!((line = in.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer.append(line);
        }
        String content=buffer.toString();
        return content;
    }
}
