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
import cn.cedar.data.InParams;
import cn.cedar.data.annotation.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author cedar12.zxd@qq.com
 */
public class ParameterParser extends InParams{

    private ParameterParser(){};

    public static String parse(Method method, Object[] args){
        if(args==null||args.length==0){
            return EMPTY_SYMBOL;
        }
        Map<String,Object> paramsMap=new HashMap<>();
        Annotation[][] annos=method.getParameterAnnotations();
        for(int i=0;i<args.length;i++){
            for (int j = 0; j < annos[i].length; j++) {
                Annotation anno = annos[i][j];
                if (anno != null && anno instanceof Param) {
                    Param param = (Param) anno;
                    InParams.in(paramsMap,param.value().trim(),args[i],false);
                }
            }
        }
        InParams.in(paramsMap,ARGS_SYMBOL,args,false);
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
