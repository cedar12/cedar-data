package cn.cedar.data;

import cn.cedar.data.actuator.StatementActuator;
import cn.cedar.data.expcetion.DynamicMethodSqlReferenceException;
import cn.cedar.data.expcetion.NoMatchMethodException;
import cn.cedar.data.parser.CedarDataFileParser;
import cn.cedar.data.parser.ExpressionParser;
import cn.cedar.data.parser.ParameterParser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
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
    private void init(Class<?> cls) {
        CedarDataFileParser.parser(cls);
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
        if(displaySql&&getEnv().equals(ENV_CEDAR_DATA)){
            System.err.println(sql);
        }
        return StatementActuator.perform(method,sql);
    }

}
