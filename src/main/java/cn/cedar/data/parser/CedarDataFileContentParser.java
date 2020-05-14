package cn.cedar.data.parser;

import cn.cedar.data.CedarDataBase;
import cn.cedar.data.expcetion.ReferenceException;
import cn.cedar.data.expcetion.SyntaxException;
import cn.cedar.data.loader.CedarDataLoader;
import cn.cedar.data.struct.Block;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cedar12.zxd@qq.com
 */
public class CedarDataFileContentParser extends CedarDataBase {
	
	public String getDef(String key) {
		return defs.get(key);
	}

	private static void parseSql(String msg,List<String> list){
		if(msg==null||msg.isEmpty()){return;}
		char[] chars=msg.toCharArray();
		int s=-1,e=0;
		s=msg.lastIndexOf(S_SYMBOL);
		if(s<=-1){
			return;
		}
		for(int i=s;i<chars.length;i++){
			if(chars[i]==E_SYMBOL){
				e=i;
				break;
			}
		}
		chars[s]=S_TMP_SYMBOL;
		chars[e]=E_TMP_SYMBOL;
		if(s>0&&chars[s-1]==EXP_FLAG_SYMBOL){
			list.add((msg.substring(s,e+1).replaceAll(String.valueOf(S_TMP_SYMBOL),String.valueOf(S_SYMBOL)).replaceAll(String.valueOf(E_TMP_SYMBOL),String.valueOf(E_SYMBOL)))+SPLIT_SYMBOL+s+COLON_SYMBOL+e);
		}
		parseSql(new String(chars),list);
	}


	public void parse(Class<?> cls){
		Method[] methods=cls.getDeclaredMethods();
		for (Block block : blocks) {
			for(Method method:methods) {
				if(block.getName().equals(method.getName())){
					String sql=block.getBody().trim();
					List<String> list=new ArrayList<>();
					parseSql(sql,list);
					for (int i = 0; i < list.size(); i++) {
						String line=list.get(i);
						String[] indexs=line.substring(line.lastIndexOf(",")+1).split(COLON_SYMBOL);
						sql=sql.substring(0,Integer.parseInt(indexs[0])-1)+placeholderSymbol(i)+sql.substring(Integer.parseInt(indexs[1])+1);
					}
					block.setSql(sql);
					block.setExpress(list);
					block.setTarget(cls);
				}
			}
		}

	}

	
	private void importParser(String content,int row,int layer) {
		if(layer>=MAX_LAYER) {
			return;
		}
		String trimContent=content.trim();
		Pattern p=Pattern.compile(PATTERN_IMPORT);
		Matcher m=p.matcher(trimContent);
		if(m.find()) {
			System.out.println(m.group(1));
			if(m.groupCount()>1) {
				String path=m.group(1).trim();
				String[] paths=path.split(CONTENT_COMMA);
				for (String string : paths) {
					imports.add(string);
					List<String> importContent=CedarDataLoader.load(string+FILE_SUFFIX, CedarDataFileContentParser.class);
					importContent=parseMultiLineAnnotation(importContent);
					startRow=-1;
					for (int i = 0; i < importContent.size(); i++) {
						String c=importContent.get(i);
						execute(c,i,layer);
					}
				}
				
			}else {
				throw new SyntaxException(KEYWORD_IMPORT,content,row,1);
			}
		}else {
			throw new SyntaxException(KEYWORD_IMPORT,content,row,1);
		}
		return;
	}

	private String defParser(String content,int row) {
		String trimContent=content.trim();
		trimContent=defRelolver(trimContent,row);
		Pattern p=Pattern.compile(PATTERN_DEF);
		Matcher m=p.matcher(trimContent);
		if(m.find()) {
			if(m.groupCount()>1) {
				String value=m.group(3).trim();
				if(value.startsWith("\"")&&value.endsWith("\"")) {
					value=value.substring(1, value.length()-1);
				}
				defs.put(m.group(1).trim(), value);
				
			}else {
				throw new SyntaxException(KEYWORD_DEF,content,row,1);
			}
		}else {
			throw new SyntaxException(KEYWORD_DEF,content,row,1);
		}
		return null;
	}

	private String defRelolver(String line,int row) {
		String newLine=line;
		Pattern p=Pattern.compile(PATTERN_RELOLVER);
		Matcher m=p.matcher(line);
		while(m.find()) {
			for (int i = 0; i < m.groupCount(); i++) {
				String var=m.group(i);
				String key=var.substring(2, var.length()-1).trim();
				String value=defs.get(key);
				if(value==null) {
					throw new ReferenceException(key,line,row,(line.indexOf(var)+1));
				}else {
					newLine=newLine.replace(var, value);
				}
			}
		}
		return newLine;
	}


	private static List<String> parseMultiLineAnnotation(List<String> inputs) {
		Pattern annoPattern = Pattern.compile(PATTERN_ANNO,Pattern.DOTALL);
		Pattern spacePattern = Pattern.compile(PATTERN_SPACE,Pattern.DOTALL);
		String input=CONTENT_EMPTY;
		for (String string : inputs) {
			input+=string+CONTENT_BR;
		}
		String output=input;
		Matcher annoMatcher=annoPattern.matcher(input);
		while(annoMatcher.find()) {
			Matcher spaceMatcher=spacePattern.matcher(annoMatcher.group(0));
			String a=spaceMatcher.replaceAll(CONTENT_EMPTY_ONE);
			output=output.replace(annoMatcher.group(0), a);
		}
		return Arrays.asList(output.split(CONTENT_BR));
	}

	private static boolean isBegin(String input) {
		Matcher m=Pattern.compile(PATTERN_BLOCK_BEGIN).matcher(input);
		return m.find();
	}
	private static boolean isEnd(String input) {
		Matcher m=Pattern.compile(PATTERN_BLOCK_END).matcher(input);
		return m.find();
	}

	private void parseBlock(String input,int startLine,int endLine) {
		Matcher m=Pattern.compile(PATTERN_BLOCK,Pattern.DOTALL).matcher(input);
		while(m.find()) {
			String key=m.group(1);
			String value=m.group(3);
			Block b=new Block();
			String[] keys=key.trim().replaceAll("\\s+", CONTENT_COMMA).split(CONTENT_COMMA);
			b.setName(keys[0]);
			if(keys.length>1){
				b.setType(keys[1]);
			}
			b.setBody(value);
			b.setStartLineNum(startLine);
			b.setEndLineNum(endLine);
			blocks.add(b);
		}
	}

	private static List<String> wipePrivate(List<String> inputs) {
		Pattern p = Pattern.compile(PATTERN_PRIVATE);
		String input=CONTENT_EMPTY;
		for (String string : inputs) {
			input+=string+CONTENT_BR;
		}
		Matcher m=p.matcher(input);
		input=m.replaceAll(CONTENT_EMPTY_FIVE);
		return Arrays.asList(input.split(CONTENT_BR));
	}

	public void parse(List<String> contentsList) {
		contentsList=wipePrivate(contentsList);
		contents=parseMultiLineAnnotation(contentsList);
		for (int i = 0; i < contents.size(); i++) {
			String content=contents.get(i);
			execute(content,i,0);
		}
	}


	private void execute(String content,int i,int layer) {
		if(content.trim().isEmpty()) {
			return;
		}else if(content.trim().startsWith(KEYWORD_ANNO)) {
			return;
		}else if(content.indexOf(KEYWORD_ANNO)>-1) {
			String c=content.substring(0,content.indexOf(KEYWORD_ANNO));
			execute(c,i,layer);
			return;
		}else if(content.trim().startsWith(KEYWORD_IMPORT+CONTENT_EMPTY_ONE)) {
			layer++; 
			importParser(content,i+1,layer);
		}else if(content.trim().startsWith(KEYWORD_DEF+CONTENT_EMPTY_ONE)) {
			defParser(content,i+1);
		}else if(content.trim().startsWith(KEYWORD_PRIVATE+CONTENT_EMPTY_ONE)) {
			return;
		}else if(isBegin(content)&&isEnd(content)&&startRow==-1){
			String con=defRelolver(content,i+1);
			parseBlock(con,startRow,i);
		}else if(isBegin(content)) {
			startRow=i;
		}else if(isEnd(content)&&startRow!=-1) {
			String sql="";
			for (int j = startRow; j <= i; j++) {
				String con=contents.get(j);
				con=defRelolver(con,j+1);
				sql+=con;
				startRow++;
			}
			startRow=-1;
			parseBlock(sql,startRow,i);
		}
	}
	
	
	
}
