package cn.cedar.data;

import javax.script.ScriptEngineManager;
import java.util.regex.Pattern;

/**
 * @author 413338772@qq.com
 */
class HandleConstant {

    private HandleConstant(){};

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
    protected static final String EVAL_NAME_SYMBOL="parse_express";
    protected static final String FLAG_SYMBOL="!S ";


    protected static final int TYPE_INT=0;
    protected static final int TYPE_INTEGER=1;
    protected static final int TYPE_LONG=2;
    protected static final int TYPE_LONG_=3;
    protected static final int TYPE_OTHER=4;

    protected static Pattern ANNOTATION = Pattern.compile("\\/\\*.*?\\*\\/",Pattern.DOTALL);
    protected static ScriptEngineManager MANAGER = new ScriptEngineManager();

}
