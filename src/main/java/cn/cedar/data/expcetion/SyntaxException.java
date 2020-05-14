package cn.cedar.data.expcetion;

/**
 * @author cedar12.zxd@qq.com
 */
public class SyntaxException extends CedarDataRuntimeException{

	public SyntaxException(String msg) {
		super(msg);
	}
	
	public SyntaxException(String type,String content,int line,int col) {
		super(type+" syntax error\ntat <"+content+">:"+line+":"+col);
	}
	
}
