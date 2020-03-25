package cn.cedar.data.parser;

import cn.cedar.data.HandlerConstant;
import cn.cedar.data.InParams;
import cn.cedar.data.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author 413338772@qq.com
 */
public class ParameterParser extends InParams{

    private ParameterParser(){};

    public static String parse(Method method, Object[] args){
        if(args==null||args.length==0){
            return EMPTY_SYMBOL;
        }
        Map<String,Object> paramsMap=new HashMap<>();
        Annotation[][] annos=method.getParameterAnnotations();
        List<Object> tmpArgs=new ArrayList<>();
        for (int i=0;i<args.length;i++){
            if(args[i] instanceof Map){
                Set<Map.Entry<String, Object>> entrySet = ((Map) args[i]).entrySet();
                for (Map.Entry<String, Object> entry : entrySet) {
                    InParams.in(paramsMap,entry.getKey().trim(),entry.getValue(),false);
                }
            }else {
                tmpArgs.add(args[i]);
            }
        }
        for(int i=0;i<tmpArgs.size();i++){
            for (int j = 0; j < annos[i].length; j++) {
                Annotation anno = annos[i][j];
                if (anno != null && anno instanceof Param) {
                    Param param = (Param) anno;
                    InParams.in(paramsMap,param.value().trim(),tmpArgs.get(i),false);
                }
            }
        }
        String var= HandlerConstant.EMPTY_SYMBOL;
        Set<Map.Entry<String, Object>> entrySet = paramsMap.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            if(InParams.isString(entry.getValue())){
                if(entry.getValue().toString().startsWith(HandlerConstant.FLAG_SYMBOL)){
                    String value=entry.getValue().toString().replaceAll(HandlerConstant.FLAG_SYMBOL,"");
                    var += "var " + entry.getKey() + "=" + value + ";";
                }else {
                    var += "var " + entry.getKey() + "=\"" + entry.getValue() + "\";";
                }
            }else {
                var += "var " + entry.getKey() + "=" + entry.getValue() + ";";
            }
        }
        return  var;
    }

}
