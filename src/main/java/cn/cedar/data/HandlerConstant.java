package cn.cedar.data;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author 413338772@qq.com
 */
public class HandlerConstant {

    protected HandlerConstant(){};

    protected static final char START_SYMBOL=123;
    protected static final char END_SYMBOL=125;
    protected static final char S_SYMBOL=91;
    protected static final char E_SYMBOL=93;
    protected static final char WELL_SYMBOL=35;
    protected static final char SINGLE_SYMBOL=39;
    protected static final char DOUBLE_SYMBOL=34;
    protected static final String MAP_SYMBOL="map";
    protected static final String PACK_MAP_SYMBOL="java.util.Map";
    protected static final String EMPTY_SYMBOL="";
    protected static final String SELECT_SYMBOL="select";
    protected static final String JS_SYMBOL="JavaScript";
    protected static final String SPLIT_SYMBOL=",";
    protected static final String COLON_SYMBOL=":";
    protected static final String KEY_SYMBOL="KEY";
    protected static final String RETURN_SYMBOL="return";
    protected static final String ONE_EMPTY_SYMBOL=" ";
    protected static final String FUN_SYMBOL="function";
    protected static final String EVAL_NAME_SYMBOL="p_exp";
    protected static final String FLAG_SYMBOL="!S ";
    protected static final String PLACEHOLDER_SYMBOL="@?";

    protected static final String EXP_SYMBOL="express";
    protected static final String SQL_SYMBOL="sql";


    protected static final int TYPE_INT=0;
    protected static final int TYPE_INTEGER=1;
    protected static final int TYPE_LONG=2;
    protected static final int TYPE_LONG_=3;
    protected static final int TYPE_OTHER=4;

    protected static Pattern ANNOTATION = Pattern.compile("\\/\\*.*?\\*\\/",Pattern.DOTALL);
    protected static ScriptEngineManager MANAGER = new ScriptEngineManager();
    protected static ScriptEngine ENGINE = HandlerConstant.MANAGER.getEngineByName(HandlerConstant.JS_SYMBOL);


    protected static Map<Method,String> sqlMap=new HashMap<>();
    protected static Map<Method,String> returnMap=new HashMap<>();

    protected static Map<Class,Object> proxyMap=new HashMap<>();

    protected static Map<Method,Map<String,Object>> parseSqlMap=new HashMap<>();


    protected static JdbcManager jdbc=new JdbcManager();

    protected static boolean isExtended=false;
    public static void setJdbcManager(JdbcManager jdbc){
        isExtended=true;
        HandlerConstant.jdbc=jdbc;
    }

    public static JdbcManager getJdbcManager(){
        return jdbc;
    }

    protected static boolean displaySql=false;

    public static void setDisplaySql(boolean display){
        displaySql=display;
    }

    protected static String placeholderSymbol(int count){
        return String.valueOf(S_SYMBOL)+count+String.valueOf(E_SYMBOL);
    }

}
