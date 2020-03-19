package cn.cedar.data;

import java.util.regex.Pattern;

class HandleConstant {

    private HandleConstant(){};

    protected static final char START_SYMBOL=123;
    protected static final char END_SYMBOL=125;
    protected static final String MAP_SYMBOL="map";
    protected static final String PACK_MAP_SYMBOL="java.util.Map";
    protected static final String EMPTY_SYMBOL="";
    protected static final String SELECT_SYMBOL="select";
    protected static final String JS_SYMBOL="JavaScript";
    protected static final String SPLIT_SYMBOL=",";

    protected static final int TYPE_INT=0;
    protected static final int TYPE_INTEGER=1;
    protected static final int TYPE_LONG=2;
    protected static final int TYPE_LONG_=3;
    protected static final int TYPE_OTHER=4;

    protected static Pattern ANNOTATION = Pattern.compile("\\/\\*.*?\\*\\/",Pattern.DOTALL);

}
