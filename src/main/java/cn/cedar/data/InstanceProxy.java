package cn.cedar.data;

import cn.cedar.data.actuator.StatementActuator;
import cn.cedar.data.expcetion.DynamicMethodSqlReferenceException;
import cn.cedar.data.expcetion.NoMatchMethodException;
import cn.cedar.data.parser.ExpressionParser;
import cn.cedar.data.parser.ParameterParser;
import cn.cedar.data.parser.StatementParser;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
        String path="";
        for (int i=0;i<paths.length;i++){
            path+=paths[i];
            if(i<paths.length-1){
                path+="\\"+File.separator;
            }
        }
        return path;
    }

    /**
     *
     * @param cls
     */
    private void init(Class<?> cls){
        URL url = ProxyHandler.class.getClassLoader().getResource("");
        File file = new File(url.getPath()+getMapperPath(cls));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String content= HandlerConstant.EMPTY_SYMBOL;
        try{
            FileChannel inFc = new FileInputStream(file).getChannel();
            buffer.clear();
            int length = inFc.read(buffer);
            content=new String(buffer.array(),0,length,"UTF-8");
            inFc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(!HandlerConstant.EMPTY_SYMBOL.equals(content)){
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
            String[] expAndIndex=exp.split(SPLIT_SYMBOL);
            exp=expAndIndex[0].substring(1,expAndIndex[0].length()-1);
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
