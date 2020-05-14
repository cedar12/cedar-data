package cn.cedar.data.expcetion;

/**
 * @author cedar12.zxd@qq.com
 */
public class ReferenceException extends CedarDataRuntimeException{

	public ReferenceException(String msg) {
		super(msg);
	}
	
	public ReferenceException(String key,String content,int line,int col) {
		super(key+" is not defined\n\tat <"+content+">:"+line+":"+col);
	}
	
}

