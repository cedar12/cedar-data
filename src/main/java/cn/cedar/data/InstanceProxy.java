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
package cn.cedar.data;

import cn.cedar.data.actuator.StatementActuator;
import cn.cedar.data.expcetion.DynamicMethodSqlReferenceException;
import cn.cedar.data.expcetion.NoMatchMethodException;
import cn.cedar.data.parser.CedarDataFileContentParser;
import cn.cedar.data.parser.CedarDataFileParser;
import cn.cedar.data.parser.ExpressionParser;
import cn.cedar.data.parser.ParameterParser;
import cn.cedar.data.struct.Block;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author cedar12.zxd@qq.com
 */
public final class InstanceProxy extends HandlerConstant implements InvocationHandler {

    private CedarDataFileContentParser cedarData=new CedarDataFileContentParser();

    /**
     * @param cls
     */
    protected InstanceProxy(Class<?> cls){
        init(cls);
    }

    /**
     *
     * @param cls
     * @return
     */
    private String getMapperPath(Class<?> cls){
        String[] paths=cls.getName().split("\\.");
        String path=cls.getName().replaceAll("\\.",FILE_SPLIT_SYMBOL);
        return path;
    }

    /**
     *
     * @param cls
     */
    private void init(Class<?> cls) {
        cedarData=CedarDataFileParser.parser(cls,cedarData);
    }

    /**
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            try {
                return method.invoke(this, args);
            }catch (InvocationTargetException e){
                throw e.getTargetException();
            }
        }
        //return exec(proxy.getClass(),method,args);
        return excute(proxy.getClass(),method,args);
    }

    /**
     *
     * @param method
     * @param args
     * @return
     * @throws NoMatchMethodException DynamicMethodSqlReferenceException
     */
    private Object exec(Class<?> cls,Method method,Object[] args) throws NoMatchMethodException, DynamicMethodSqlReferenceException {
        Map<String,Object> sqlMap=parseSqlMap.get(method);
        if(sqlMap==null){
            throw new NoMatchMethodException(method);
        }
        String sql=String.valueOf(sqlMap.get(SQL_SYMBOL));
        String var=ParameterParser.parse(method,args);
        List<String> exps= (List<String>) sqlMap.get(EXP_SYMBOL);
        for (int i = 0; i < exps.size(); i++) {
            String exp=exps.get(i);
            String ep=exp.substring(0,exp.lastIndexOf(SPLIT_SYMBOL));
            exp=ep.substring(1,ep.length()-1);
            try {
                Object res = ExpressionParser.parse(exp, var);
                sql=sql.replace(placeholderSymbol(i),String.valueOf(res));
            }catch (DynamicMethodSqlReferenceException e){
                throw new DynamicMethodSqlReferenceException(method,e.getMessage());
            }
        }
        if(displaySql&&getEnv().equals(ENV_CEDAR_DATA)){
            System.err.println(sql);
        }
        return StatementActuator.perform(method,sql);
    }

    private Object excute(Class<?> cls,Method method,Object[] args) throws NoMatchMethodException, DynamicMethodSqlReferenceException {
        List<Block> blocks=cedarData.getBlocks();
        Block block=null;
        for (Block b : blocks) {
            if(b.getName().equals(method.getName())){
                block=b;
            }
        }
        if(null==block){
            throw new NoMatchMethodException(method);
        }

        String sql=block.getExpSql();
        String var=ParameterParser.parse(method,args);
        List<String> exps= block.getExpress();
        for (int i = 0; i < exps.size(); i++) {
            String exp=exps.get(i);
            String ep=exp.substring(0,exp.lastIndexOf(SPLIT_SYMBOL));
            exp=ep.substring(1,ep.length()-1);
            try {
                Object res = ExpressionParser.parse(exp, var);
                sql=sql.replace(placeholderSymbol(i),String.valueOf(res));
            }catch (DynamicMethodSqlReferenceException e){
                throw new DynamicMethodSqlReferenceException(method,e.getMessage());
            }
        }
        block.setSql(sql);
        block.setArgs(args);
        block.setMethod(method);
        return StatementActuator.perform(method,block,cedarData);
    }

}
