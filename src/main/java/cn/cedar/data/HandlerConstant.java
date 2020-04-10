package cn.cedar.data;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author 413338772@qq.com
 */
public class HandlerConstant {

    protected HandlerConstant(){};



    protected static final String ENV_CEDAR_DATA="cedar-data";
    protected static final String ENV_CEDAR_DATA_SPRING="cedar-data-spring";
    protected static final String ENV_CEDAR_DATA_SPRING_BOOT_STARTER="cedar-data-spring-boot-starter";

    protected static String env=ENV_CEDAR_DATA;

    public static String getEnv() {
        return env;
    }

    public static void setEnv(String env) {
        HandlerConstant.env = env;
    }


    protected static final char START_SYMBOL=123;
    protected static final char END_SYMBOL=125;
    protected static final char S_SYMBOL=91;
    protected static final char E_SYMBOL=93;
    protected static final char WELL_SYMBOL=35;
    protected static final char SINGLE_SYMBOL=39;
    protected static final char DOUBLE_SYMBOL=34;
    protected static final char S_TMP_SYMBOL='@';
    protected static final char E_TMP_SYMBOL='`';
    protected static final char EXP_FLAG_SYMBOL='#';
    protected static final String FILE_SPLIT_SYMBOL="/";
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

    protected static final String ARGS_SYMBOL="args";

    protected static final String EXP_SYMBOL="express";
    protected static final String SQL_SYMBOL="sql";

    protected static final String DATE_FORMAT="Date.prototype.format = function (fmt) { var o = {'M+': this.getMonth() + 1, 'd+': this.getDate(), 'h+': this.getHours(), 'm+': this.getMinutes(), 's+': this.getSeconds(), 'q+': Math.floor((this.getMonth() + 3) / 3), 'S': this.getMilliseconds() }; if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length)); for (var k in o) if (new RegExp('(' + k + ')').test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length))); return '\\''+fmt+'\\'';};";
    protected static final String STRING_TO="String.prototype.to=function(){ var s=this.toString(); if(s[0]=='\\''&&s[s.length-1]=='\\''){ return s.substring(1,s.length-1); }else if(s[0]=='\\''&&s[s.length-1]!='\\''){ return s+'\\''; }else if(s[0]!='\\''&&s[s.length-1]=='\\''){ return '\\''+s; }else{ return '\\''+s+'\\''; }};";

    protected static final int TYPE_INT=0;
    protected static final int TYPE_INTEGER=1;
    protected static final int TYPE_LONG=2;
    protected static final int TYPE_LONG_=3;
    protected static final int TYPE_OTHER=4;


    protected static Map<Class<?>,Map<String,String>> setMap=new LinkedHashMap<>();

    protected static final String KEYWORD_CONST="const";
    protected static final String KEYWORD_DEF="def";
    protected static final String KEYWORD_IMPORT="import";
    protected static final String KEYWORD_PRIVATE="private";

    protected static final String FILE_SUFFIX=".cd";




    protected static int MAX_LAYER=5;

    public static void setMaxLayer(int max){
        if(max>0){
            MAX_LAYER=max;
        }
    }


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
