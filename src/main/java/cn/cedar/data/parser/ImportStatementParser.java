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
import cn.cedar.data.expcetion.ImportLevelExcpetion;
import cn.cedar.data.expcetion.NotFoundDynamicMethodSqlException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cedar12.zxd@qq.com
 */
public class ImportStatementParser extends HandlerConstant{

    public static String importParser(String content,Class<?> cls,int layer) throws NullPointerException{
        Matcher m=Pattern.compile(KEYWORD_IMPORT+"(\\s+?)(.+?);", Pattern.DOTALL|Pattern.MULTILINE).matcher(content);
        List<String> imports=new ArrayList<>();
        String tmp=content;
        while(m.find()) {
            if(m.groupCount()>1) {
                tmp=tmp.replace(m.group(0), EMPTY_SYMBOL);
                imports.add(m.group(0).replace(KEYWORD_IMPORT, EMPTY_SYMBOL).replace(";", EMPTY_SYMBOL).trim());
            }
        }
        tmp=filterPrivate(tmp);
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

    private static String filterPrivate(String content){
        Matcher m=Pattern.compile(KEYWORD_PRIVATE+"(\\s+?)"+KEYWORD_DEF+"(\\s+?)(.+?)=(.+?);", Pattern.DOTALL|Pattern.MULTILINE).matcher(content);
        content=m.replaceAll(EMPTY_SYMBOL);
        m=Pattern.compile(KEYWORD_PRIVATE+"(\\s+?)(.+?)\\}(\\s*?);", Pattern.DOTALL|Pattern.MULTILINE).matcher(content);
        content=m.replaceAll(EMPTY_SYMBOL);
        return content;
    }

    public static String load(String path,Class<?> cls){
        if(!path.startsWith(FILE_SPLIT_SYMBOL)){
            path = FILE_SPLIT_SYMBOL + path;
        }
        if(!path.endsWith(FILE_SUFFIX)){
            path=path+FILE_SUFFIX;
        }
        if(!path.startsWith("/")){
            path="/"+path;
        }
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(cls.getResourceAsStream(path)));
            String line = EMPTY_SYMBOL;
            while (true) {
                try {
                    if (!((line = in.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buffer.append(line);
            }
        }catch (NullPointerException e){
            throw new NotFoundDynamicMethodSqlException(cls+" Not Found  File:"+path);
        }
        String content=buffer.toString();
        return content;
    }
}
