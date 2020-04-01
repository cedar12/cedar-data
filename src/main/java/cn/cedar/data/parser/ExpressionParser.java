package cn.cedar.data.parser;

import cn.cedar.data.HandlerConstant;
import cn.cedar.data.expcetion.DynamicMethodSqlReferenceException;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 *
 * @author 413338772@qq.com
 */
public class ExpressionParser extends HandlerConstant {

    private ExpressionParser(){};

    private static String functions(String exp,String var){
        String preFun=var.contains("new Date(")?DATE_FORMAT:EMPTY_SYMBOL;
        String toFun=exp.contains(".to(")? STRING_TO:EMPTY_SYMBOL;
        return preFun+toFun;
    }

    public static Object parse(String exp,String var) throws DynamicMethodSqlReferenceException {
        String isReturnStr=exp.contains(HandlerConstant.RETURN_SYMBOL)? HandlerConstant.EMPTY_SYMBOL: HandlerConstant.RETURN_SYMBOL;

        try {
            ENGINE.eval(functions(exp,var)+HandlerConstant.FUN_SYMBOL+ HandlerConstant.ONE_EMPTY_SYMBOL + HandlerConstant.EVAL_NAME_SYMBOL + "()" + HandlerConstant.START_SYMBOL+var+isReturnStr+ HandlerConstant.ONE_EMPTY_SYMBOL+exp+ HandlerConstant.END_SYMBOL);
        } catch (ScriptException e) {
            throw new DynamicMethodSqlReferenceException("Expression: #[" + exp + "]  " + e.getMessage().replace("sun.org.mozilla.javascript.internal.", "").replaceAll(HandlerConstant.FUN_SYMBOL+ HandlerConstant.ONE_EMPTY_SYMBOL + HandlerConstant.EVAL_NAME_SYMBOL + "()",""));
        }
        Invocable invocable = (Invocable) ENGINE;
        Object o = null;
        try {
            o = invocable.invokeFunction(HandlerConstant.EVAL_NAME_SYMBOL);
        } catch (ScriptException e) {
            throw new DynamicMethodSqlReferenceException("Expression: #["+exp+"]  "+e.getMessage().replace("sun.org.mozilla.javascript.internal.","").replaceAll(HandlerConstant.FUN_SYMBOL+ HandlerConstant.ONE_EMPTY_SYMBOL + HandlerConstant.EVAL_NAME_SYMBOL + "()",""));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return o;
    }

}
