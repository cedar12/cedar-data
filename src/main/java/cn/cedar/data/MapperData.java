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
package cn.cedar.data;

import cn.cedar.data.expcetion.SyntaxException;
import cn.cedar.data.parser.CedarDataFileContentParser;
import cn.cedar.data.struct.Type;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author cedar12.zxd@qq.com
 */
public class MapperData {
	
	private Map<String,String> defs=new HashMap<String,String>();
	private CedarDataFileContentParser cedarData;

	private List<Map<String,Object>> dataList=new ArrayList<>();
	private Type type=new Type();
	
	public MapperData(List<Map<String,Object>> dataList) {
		this.dataList=dataList;
	}
	public MapperData def(String key,String value) {
		defs.put(key, value);
		return this;
	}
	public MapperData cedarData(CedarDataFileContentParser cedarData){
		this.cedarData=cedarData;
		return this;
	}
	
	public List<Object> parse(String type) {
		if(type.startsWith(CedarDataBase.ATTR_CLASS+CedarDataBase.ATTR_SPLIT)) {
			types(type,this.type);
			List<Object> list=handle();
			return list;
		}
		return null;
	}
	
	public static Object converType(Class<?> cls,Object value){
		if(value==null||(InParams.isString(value)&&value.toString().trim().equalsIgnoreCase(CedarDataBase.STR_NULL))){
			return null;
		}
		if(InParams.isByte(cls)){
			return Byte.parseByte(String.valueOf(value));
		}else if(InParams.isShort(cls)){
			return Short.parseShort(String.valueOf(value));
		}else if(InParams.isInt(cls)){
			return Integer.parseInt(String.valueOf(value));
		}else if(InParams.isLong(cls)){
			return Long.parseLong(String.valueOf(value));
		}else if(InParams.isFloat(cls)){
			return Float.parseFloat(String.valueOf(value));
		}else if(InParams.isDouble(cls)){
			return Float.parseFloat(String.valueOf(value));
		}else if(InParams.isBigDecimal(cls)){
			return new BigDecimal(String.valueOf(value));
		}else if(InParams.isString(cls)){
			return String.valueOf(value);
		}else{
			return value;
		}
	}
	
	public static boolean isNull(Object obj) {
		return obj==null;
	}
	
	public static String fieldNameToMethodSetter(String name) {
		return CedarDataBase.METHOD_SET+name.substring(0, 1).toUpperCase()+name.substring(1);
	}
	public static String fieldNameToMethodGetter(String name) {
		return CedarDataBase.METHOD_GET+name.substring(0, 1).toUpperCase()+name.substring(1);
	}
	
	public static void fieldSet(Field field,Object obj,Object value) {
		String  methodName=fieldNameToMethodSetter(field.getName());
		Method method=null;
		try {
			method=obj.getClass().getMethod(methodName,field.getType());
			method.invoke(obj, converType(field.getType(),value));
		} catch (NoSuchMethodException e1) {
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if(isNull(method)) {
			field.setAccessible(true);
			try {
				field.set(obj, converType(field.getType(),value));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			field.setAccessible(false);
		}
	}
	
	public static Object fieldGet(Object obj,String name) {
		Object result=null;
		String getterName=fieldNameToMethodGetter(name);
		try {
			result=obj.getClass().getMethod(getterName).invoke(obj);
		} catch (NoSuchMethodException e1) {
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if(isNull(result)) {
			try {
				Field field=obj.getClass().getDeclaredField(name);
				field.setAccessible(true);
				result=field.get(obj);
				field.setAccessible(false);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public void setObjectValue(Object obj,Map<String,Object> map,Type type) {
		Class<?> cls=obj.getClass();
		for (Entry<String,Object> entry : map.entrySet()) {
			String key=entry.getKey();
			Object value=entry.getValue();
			if(type.getKeyNames().size()<1) {
				Field[] fields=cls.getDeclaredFields();
				for (Field field : fields) {
					if(key.equals(field.getName())) {
						fieldSet(field,obj,value);
						break;
					}
				}
			}else {
				try {
					String aliasKey=null;
					for (int i = 0; i < type.getKeyNames().size(); i++) {
						String keyName=type.getKeyNames().get(i);
						if(type.getFieldNames().get(i).equals(key)) {
							aliasKey=keyName;
							break;
						}
					}
					if(aliasKey==null) {
						aliasKey=key;
					}
					Field field=cls.getDeclaredField(aliasKey);
					fieldSet(field,obj,value);
				} catch (NoSuchFieldException e) {
					//e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void removeDuplicate(List<Object> list,String primaryKey) {
		List<Object> tempList=new ArrayList<>();
		for (Object obj : list) {
			String id=str(fieldGet(obj, primaryKey));
			boolean isAdd=true;
			for (Object object : tempList) {
				if(id.equals(str(fieldGet(object,primaryKey)))) {
					isAdd=false;
					break;
				}
			}
			if(isAdd) {
				tempList.add(obj);
			}
		}
		list.clear();
		list.addAll(tempList);
	}

	public static String str(Object obj) {
		return String.valueOf(obj);
	}
	
	private void oo(List<Object> list,List<Map<String,Object>> dataList,Type type) {
		for (Map<String, Object> map : dataList) {
			Object firstId=map.get(type.getPrimaryFieldName());
			for (Object obj : list) {
				Object id=fieldGet(obj,this.type.getPrimaryKeyName());
				if(str(firstId).equals(str(id))) {
					Object subObj=type.getTargetInstance();
					setObjectValue(subObj,map,type);
					try {
						fieldSet(obj.getClass().getDeclaredField(type.getRelationKeyName()),obj,subObj);
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void om(List<Object> list,List<Map<String,Object>> dataList,Type type) {
		String primaryKey=this.type.getPrimaryKeyName();
		for (Object obj : list) {
			Object id=fieldGet(obj,primaryKey);
			for (Map<String, Object> map : dataList) {
				String firstId=str(map.get(type.getFirstFieldName()));
				if(firstId.equals(str(id))) {
					List<Object> subList=(List<Object>) fieldGet(obj,type.getRelationKeyName());
					Object subObj=type.getTargetInstance();
					setObjectValue(subObj,map,type);
					subList.add(subObj);
				}
			}
		}
	}
	
	private void mm(List<Object> list,List<Map<String,Object>> dataList,Type type) {
		String primaryKey=this.type.getPrimaryKeyName();
		for (Map<String, Object> map : dataList) {
			Object firstId=map.get(type.getFirstFieldName());
			Object secondId=map.get(type.getSecondFieldName());
			for (Object obj : list) {
				Object subObj=type.getTargetInstance();
				setObjectValue(subObj,map,type);
				Object id=fieldGet(obj,primaryKey);
				Object id2=fieldGet(subObj,type.getPrimaryKeyName());
				if(str(firstId).equals(str(id))&&str(secondId).equals(str(id2))) {
					Object relationObj=fieldGet(obj,type.getRelationKeyName());
					if(relationObj==null) {
						Field field;
						try {
							field = this.type.getTargetClass().getDeclaredField(type.getRelationKeyName());
							fieldSet(field,obj , new ArrayList<>());
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						}
					}
					if(relationObj instanceof List) {
						List<Object> subList=(List<Object>)relationObj; 
						subList.add(subObj);
					}
				}
			}
		}
	}
	
	private List<Object> handle() {
		List<Object> list=new ArrayList<>();
		for (Map<String, Object> map : dataList) {
			Object targetObject=type.getTargetInstance();
			setObjectValue(targetObject,map,type);
			list.add(targetObject);
		}
		removeDuplicate(list,type.getPrimaryKeyName());
		for (Type type : type.getTypes()) {
			if(type.getType()== Type.ONE_TO_ONE) {
				oo(list,dataList,type);
			}else if(type.getType()==Type.ONE_TO_MANY) {
				om(list,dataList,type);
			}else if(type.getType()==Type.MANY_TO_MANY) {
				mm(list,dataList,type);
			}else {
				
			}
		}
		return list;
	}
	
	
	private void types(String typeStr,Type typeObj) {
		String[] types=typeStr.split(CedarDataBase.TYPE_SPLIT);
		for (int i = 0; i < types.length; i++) {
			String type=types[i];
			String[] params=type.split(CedarDataBase.ATTR_SPLIT);
			if(params[0].equals(CedarDataBase.ATTR_CLASS)) {
				typeObj.setTarget(params[1]);
			}else if(params[0].equals(CedarDataBase.ATTR_ONE_TO_ONE)) {
				Type subType=new Type();
				subType.setType(Type.ONE_TO_ONE);
				subType.setRelationKeyName(params[1]);
				String defKey=params[params.length-1];
				types(getDef(defKey),subType);
				typeObj.add(subType);
			}else if(params[0].equals(CedarDataBase.ATTR_ONE_TO_MANY)) {
				Type subType=new Type();
				subType.setType(Type.ONE_TO_MANY);
				subType.setRelationKeyName(params[1]);
				subType.setFirstFieldName(params[2]);
				String defKey=params[params.length-1];
				types(getDef(defKey),subType);
				typeObj.add(subType);
			}else if(params[0].equals(CedarDataBase.ATTR_MANY_TO_MANY)) {
				Type subType=new Type();
				subType.setType(Type.MANY_TO_MANY);
				subType.setRelationKeyName(params[1]);
				subType.setFirstFieldName(params[2]);
				subType.setSecondFieldName(params[3]);
				String defKey=params[params.length-1];
				types(getDef(defKey),subType);
				typeObj.add(subType);
			}else {
				if(params.length>1) {
					typeObj.put(params[0], params[1]);
				}else{
					error(typeStr);
				}
			}
		}
	}

	public String getDef(String defKey){
		if(!isNull(cedarData)){
			return cedarData.getDef(defKey);
		}
		return defs.get(defKey);
	}

	private static void error(String msg){
		throw new SyntaxException(msg);
	}
}
