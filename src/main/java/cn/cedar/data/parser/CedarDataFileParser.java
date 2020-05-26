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
import cn.cedar.data.annotation.CedarData;
import cn.cedar.data.loader.CedarDataLoader;

import java.util.List;

/**
 * @author cedar12.zxd@qq.com
 */
public class CedarDataFileParser extends HandlerConstant{

    public static void parser(Class<?> cls){
        String path="";
        CedarData anno=cls.getAnnotation(CedarData.class);
        if(anno!=null&&!anno.cd().trim().isEmpty()){
            if(anno.cd().startsWith(FILE_SPLIT_SYMBOL)){
                path=anno.cd();
            }else {
                path = FILE_SPLIT_SYMBOL + anno.cd();
            }
        }else{
            path=FILE_SPLIT_SYMBOL+getMapperPath(cls);
        }
        String content=ImportStatementParser.load(path,cls);
        if (!HandlerConstant.EMPTY_SYMBOL.equals(content)) {
            content += ImportStatementParser.importParser(content, cls, 0);
            content = ANNOTATION.matcher(content).replaceAll(EMPTY_SYMBOL);
            content= ConstStatementParser.parser(content,cls);
            StatementParser.parse(content, cls);
        }
    }

    public static CedarDataFileContentParser parser(Class<?> cls,CedarDataFileContentParser cedarData){
        String path="";
        CedarData anno=cls.getAnnotation(CedarData.class);
        if(anno!=null&&anno.annotation()){
            return new AnnotationParser(cls);
        }
        if (anno != null && !anno.cd().trim().isEmpty()) {
            if (anno.cd().startsWith(FILE_SPLIT_SYMBOL)) {
                path = anno.cd();
            } else {
                path = FILE_SPLIT_SYMBOL + anno.cd();
            }
        } else {
            path = FILE_SPLIT_SYMBOL + getMapperPath(cls);
        }

        List<String> content = CedarDataLoader.load(path, cls);
        cedarData.parse(content);
        cedarData.parse(cls);
        return cedarData;
    }

    private static String getMapperPath(Class<?> cls){
        String[] paths=cls.getName().split("\\.");
        String path=cls.getName().replaceAll("\\.",FILE_SPLIT_SYMBOL);
        return path;
    }

}
