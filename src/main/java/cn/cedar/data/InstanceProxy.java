package cn.cedar.data;

import cn.cedar.data.actuator.StatementActuator;
import cn.cedar.data.annotation.CedarData;
import cn.cedar.data.expcetion.DynamicMethodSqlReferenceException;
import cn.cedar.data.expcetion.NoMatchMethodException;
import cn.cedar.data.parser.ExpressionParser;
import cn.cedar.data.parser.ImportStatementParser;
import cn.cedar.data.parser.ParameterParser;
import cn.cedar.data.parser.StatementParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author 413338772@qq.com
 */
public final class InstanceProxy extends HandlerConstant implements InvocationHandler {



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
    private void init(Class<?> cls){
        String path="";
        CedarData anno=cls.getAnnotation(CedarData.class);
        if(anno!=null&&!anno.dms().trim().isEmpty()){
            if(anno.dms().startsWith(FILE_SPLIT_SYMBOL)){
                path=anno.dms();
            }else {
                path = FILE_SPLIT_SYMBOL + anno.dms();
            }
        }else{
            path=FILE_SPLIT_SYMBOL+getMapperPath(cls);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(cls.getResourceAsStream(path)));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while (true){
            try {
                if (!((line = in.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer.append(line);
        }
        String content=buffer.toString();
        if(!HandlerConstant.EMPTY_SYMBOL.equals(content)){
            content+=ImportStatementParser.importParser(content,cls,0);
            StatementParser.parse(content,cls);
        }
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
            return method.invoke(this,args);
        }
        return exec(proxy.getClass(),method,args);
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
        if(displaySql){
            System.err.println(sql);
        }
        return StatementActuator.perform(method,sql);
    }

}
