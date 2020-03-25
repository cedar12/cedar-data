package cn.cedar.data.parser;

import cn.cedar.data.HandlerConstant;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 *
 * @author 413338772@qq.com
 */
public class ExpressionParser extends HandlerConstant {

    private ExpressionParser(){};

    public static Object parse(String exp,String var){
        String isReturnStr=exp.contains(HandlerConstant.RETURN_SYMBOL)? HandlerConstant.EMPTY_SYMBOL: HandlerConstant.RETURN_SYMBOL;
        try {
            ENGINE.eval(HandlerConstant.FUN_SYMBOL+ HandlerConstant.ONE_EMPTY_SYMBOL + HandlerConstant.EVAL_NAME_SYMBOL + "()" + HandlerConstant.START_SYMBOL+var+isReturnStr+ HandlerConstant.ONE_EMPTY_SYMBOL+exp+ HandlerConstant.END_SYMBOL);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) ENGINE;
        Object o = null;
        try {
            o = invocable.invokeFunction(HandlerConstant.EVAL_NAME_SYMBOL);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return o;
    }

}
