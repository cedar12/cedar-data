/**
 *	  Copyright 2020 cedar12.zxd@qq.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.cedar.data.parser;

import cn.cedar.data.expcetion.ConditionScriptException;
import cn.cedar.data.expcetion.NotFoundParameterException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cedar12.zxd@qq.com
 */
public class ConditionParser {

	public final static String STR_SQL="sql";
	public final static String STR_ARGS="args";

	private final static String STR_IF="#if ";
	private final static String STR_ELIF="#elif ";
	private final static String STR_ELSE="#else ";
	private final static String STR_END="#end ";
	private final static String STR_SPLIT=":";
	private final static String STR_JS="js";
	private final static String STR_SYMBOL="$_";
	private final static String STR_ERR="\\(|in";
	private final static String STR_EMPTY="";
	private final static String STR_QUESTION="?";
	private final static String STR_DOLLAR="$";

	private static Pattern IF_ELIF_ELSE=Pattern.compile(STR_IF.trim()+"\\s+((.+?):(.*?))?("+STR_ELIF.trim()+"\\s+(.+?):(.*))+"+STR_END.trim(),Pattern.MULTILINE);
	private static Pattern IF_ELSE=Pattern.compile(STR_IF.trim()+"\\s+(.+?):((.*?))"+STR_END.trim(),Pattern.MULTILINE);
	private static Pattern PLACEHOLD=Pattern.compile("(\\?\\d+)|(\\$\\w+)",Pattern.MULTILINE);
	private static Pattern TEST_PLACEHOLD = Pattern.compile("(\\?\\d+)",Pattern.MULTILINE);

	private Object[] values;
	private String[] names;

	public ConditionParser(String[] names,Object[] values) {
		this.names=names;
		this.values=values;
	}
	
	public Map<String, Object> parse(String input) {
		Map<String,Object> args=handleArgs(values,names);
		String sql=condition(input,args);
		return placeholder(sql);
	}
	
	private Map<String,Object> placeholder(String input){
		Map<String,Object> map=new HashMap<>();
		List<Object> args=new ArrayList<>();
		Matcher m=PLACEHOLD.matcher(input);
		while(m.find()) {
			String group=m.group();
			input=input.replace(group, STR_QUESTION);
			int index=-1;
			String name=group.substring(1);
			for (int i = 0; i < names.length; i++) {
				if(names[i].equals(name)) {
					args.add(values[i]);
					index=i;
					break;
				}
			}
			if(index==-1) {
				try {
					args.add(values[Integer.parseInt(name)-1]);
				}catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
					throw new NotFoundParameterException(group);
				}
			}
		}
		map.put(STR_SQL, input);
		map.put(STR_ARGS, args);
		return map;
	}
	
	private static String condition(String input,Map<String,Object>args) {
		//input=elifElseEnd(input, args);
		//input=ifElseEnd(input, args);
		input=ifEnd(input,args);
		return input;
	}
	
	private static String elifElseEnd(String input,Map<String,Object> args) {
		Matcher matcher = IF_ELIF_ELSE.matcher(input);
		while (matcher.find()) {
			if(matcher.groupCount()==5) {
				if(test(matcher.group(1),args)) {
					input=input.replace(matcher.group(0), matcher.group(2));
				}else{
					String group=matcher.group(3);
					String[] elifs=group.split(STR_ELIF);
					String result=STR_EMPTY;
					for (int i = 0; i < elifs.length; i++) {
						String elif=elifs[i];
						if(elif.trim().isEmpty()) {
							continue;
						}
						if(elif.contains(STR_ELSE)) {
							String[] els=elif.split(STR_ELSE);
							if(els.length==2) {
								if(els[0].contains(STR_SPLIT)) {
									String[] exps=els[0].split(STR_SPLIT);
									if(test(exps[0],args)) {
										result=exps[1];
										break;
									}
								}
								result=els[1];
							}
						}else if(elif.contains(STR_SPLIT)) {
							String[] exps=elif.split(STR_SPLIT);
							if(test(exps[0],args)) {
								result=exps[1];
								break;
							}
						}
					}
					input=input.replace(matcher.group(0), result);
				}
			}
		}
		return input;
	}
	
	private static String ifElseEnd(String input,Map<String,Object> args) {
		Matcher matcher = IF_ELSE.matcher(input);
		while (matcher.find()) {
			if(matcher.groupCount()==3) {
				String result=STR_EMPTY;
				if(matcher.group(2).contains(STR_ELSE)) {
					String[] elses=matcher.group(2).split(STR_ELSE);
					if(test(matcher.group(1),args)) {
						result=elses[0];
					}else {
						result=elses[1];
					}
				}else {
					if(test(matcher.group(1),args)) {
						result=matcher.group(2);
					}
				}
				input=input.replace(matcher.group(0), result);
			}
		}
		return input;
	}

	private static String ifEnd(String input,Map<String,Object> args) {
		Matcher matcher = IF_ELSE.matcher(input);
		while (matcher.find()) {
			if(matcher.groupCount()==3) {
				String result=STR_EMPTY;
				if(matcher.group(2).contains(STR_ELIF.trim())) {
					String[] elses=matcher.group(2).split(STR_ELIF.trim());
					if(test(matcher.group(1),args)) {
						result=elses[0];
					}else {
						for(int i=1;i<elses.length;i++) {
							String elif=elses[i];
							String[] cond=elif.split(STR_SPLIT);
							if(cond[1].contains(STR_ELSE.trim())) {
								String[] elifelses=cond[1].split(STR_ELSE.trim());
								if(test(cond[0],args)) {
									result=elifelses[0];
									break;
								}else {
									result=elifelses[1];
									break;
								}
							}else if(test(cond[0],args)) {
								result=cond[1];
								break;
							}
						}
					}
				}else if(matcher.group(2).contains(STR_ELSE.trim())) {
					String[] elses=matcher.group(2).split(STR_ELSE.trim());
					if(test(matcher.group(1),args)) {
						result=elses[0];
					}else {
						result=elses[1];
					}
				}else {
					if(test(matcher.group(1),args)) {
						result=matcher.group(2);
					}
				}
				input=input.replace(matcher.group(0), result.trim());
			}
		}
		return input;
	}
	
	
	public static Map<String, Object> handleArgs(Object[] values,String[] names) {
		Map<String,Object> params=new HashMap<>();
		for (int i = 0; i < values.length; i++) {
			params.put(STR_SYMBOL+(i+1), values[i]);
			if(names[i]!=null&&(!names[i].trim().isEmpty())&&!names[i].trim().isEmpty()) {
				params.put(STR_DOLLAR+names[i], values[i]);
			}
		}
		return params;
	}
	
	
	private static boolean test(String test,Map<String,Object> args) {
		String tmp=test;
		Matcher matcher = TEST_PLACEHOLD.matcher(test);
		if (matcher.find()) {
			String var=matcher.group(0);
			test=test.replace(var, STR_SYMBOL+var.substring(1,var.length()));
		}
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName(STR_JS);
		for (Entry<String, Object> entry : args.entrySet()) {
			String key=entry.getKey();
			Object value=entry.getValue();
			engine.put(key, value);
		}
		try {
			Object result=engine.eval(test);
			if(result instanceof Boolean) {
				return (Boolean)result;
			}
		} catch (ScriptException e) {
			String msg=e.getMessage();
			String[] msgs=msg.split(STR_SPLIT);
			msg=msgs[msgs.length-1].split(STR_ERR)[0].replace(STR_SYMBOL, STR_QUESTION);
			throw new ConditionScriptException(msg,tmp);
		}
		return false;
	}
	
}
