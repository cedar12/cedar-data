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

    public static void parser(Class<?> cls,CedarDataFileContentParser cedarData){
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

        List<String> content= CedarDataLoader.load(path, cls);
        cedarData.parse(content);
        cedarData.parse(cls);
    }

    private static String getMapperPath(Class<?> cls){
        String[] paths=cls.getName().split("\\.");
        String path=cls.getName().replaceAll("\\.",FILE_SPLIT_SYMBOL);
        return path;
    }

}
