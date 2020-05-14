package cn.cedar.data.parser;

import cn.cedar.data.CedarDataBase;
import cn.cedar.data.InParams;
import cn.cedar.data.expcetion.SyntaxException;
import org.w3c.dom.Attr;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author cedar12.zxd@qq.com
 */
public class CedarDataORMParser extends CedarDataBase {
	private boolean isOutputNoFiledError=false;
	
	private Class<?> cls=null;
	
	private int type=0;
	
	private String primaryIdName=null;
	private String foreignIdName=null;
	
	private String subPrimaryIdName=null;
	private String subForeignIdName=null;
	
	private String ormFieldName=null;
	
	private Class<?> subCls=null;
	
	private Map<String,String> fields=new HashMap<>();
	
	private Map<String,String> subFields=new HashMap<>();
	
	private List<Map<String,Object>> mapList;
	private CedarDataFileContentParser cedarData;
	
	public CedarDataORMParser(List<Map<String,Object>> mapList) {
		this.mapList=mapList;
	}
	public CedarDataORMParser(List<Map<String,Object>> mapList,CedarDataFileContentParser cedarData) {
		this.mapList=mapList;
		this.cedarData=cedarData;
	}
	private String ormExpress="";
	private String ormSubExpress="";

	public List<Object> parse(String type) {
		type=type.trim();
		ormExpress=type;
		if(type.startsWith(START_CLASS)) {
			String[] fileds=type.split(TYPE_SPLIT);
			for(int i=0;i<fileds.length;i++) {
				fieldParse(fileds[i]);
			}
			return orm();
		}
		return null;
	}

	private void parseSub(String type) {
		type=type.trim();
		if(type.startsWith(START_CLASS)) {
			String[] fileds=type.split(TYPE_SPLIT);
			for(int i=0;i<fileds.length;i++) {
				subfieldParse(fileds[i]);
			}
			
		}
	}
	
	private List<Object> orm() {
		List<Object> result=new ArrayList<>();
		if(null==cls) {
			return result;
		}
		if(type==0) {
			List<Object> list=new ArrayList<>();
			for (Map<String, Object> map : mapList) {
				Object obj=classNewInstance(cls);
				setObjFieldValue(obj,map,false);
				list.add(obj);
			}
			result=list;
		}else if(ONE_TO_ONE==type) {
			Object obj=classNewInstance(cls);
			Object o=classNewInstance(subCls);
			List<Object> list=new ArrayList<>();
			for (Map<String,Object> map : mapList) {
				setObjFieldValue(obj,map,false);
				setObjFieldValue(o,map,true);
				setORMFieldValue(obj,o);
				list.add(obj);
			}
			result=list;
		}else if(ONE_TO_MANY==type) {
			List<Object> list=new ArrayList<>();
			List<Object> list2=new ArrayList<>();
			for(int i=0;i<mapList.size();i++) {
				Map<String,Object> map=mapList.get(i);
				int index=-1;
				for (int z=0;z<list.size();z++) {
					if(String.valueOf(getPrimaryIdValue(primaryIdName,list.get(z))).equals(String.valueOf(map.get(foreignIdName)))) {
						index=z;
						break;
					}
				}
				if(index==-1) {
					Object obj=classNewInstance(cls);
					setObjFieldValue(obj,map,false);
					list.add(obj);
					list2=new ArrayList<>();
					Object o=classNewInstance(subCls);
					setObjFieldValue(o,map,true);
					list2.add(o);
					setORMFieldValue(obj,list2);
				}else {
					Object obj=list.get(index);
					Object o=classNewInstance(subCls);
					setObjFieldValue(o,map,true);
					list2=(ArrayList) getPrimaryIdValue(ormFieldName, obj);
					list2.add(o);
					setORMFieldValue(obj,list2);
				}
			}
			result=list;
		}else if(MANY_TO_MANY==type) {
			List<Object> list=new ArrayList<>();
			List<Object> list2=new ArrayList<>();
			
			Object obj=null;
			int i=0,j=0;

			while(i<mapList.size()&&j<mapList.size()) {
				Map<String, Object> map=mapList.get(i);
				Map<String, Object> map2=mapList.get(j);
				int index=-1;
				for (int z=0;z<list.size();z++) {
					if(String.valueOf(getPrimaryIdValue(primaryIdName,list.get(z))).equals(String.valueOf(map.get(foreignIdName)))) {
						index=z;
						break;
					}
				}
				
				if(null==obj||(obj!=null&&(!String.valueOf(getPrimaryIdValue(primaryIdName,obj)).equals(String.valueOf(map2.get(foreignIdName))))&&index==-1)) {
					obj=classNewInstance(cls);
					setObjFieldValue(obj,map,false);
					isOutputNoFiledError=true;
					list.add(obj);
					list2=new ArrayList<>();
					i++;
				}
				
				if(null!=obj) {
					if(index!=-1) {
						obj=list.get(index);
						list2=(List<Object>) getPrimaryIdValue(ormFieldName,obj);
						i++;
					}
					int subIndex=-1;
					for (int l = 0; l < list2.size(); l++) {
						Object o=list2.get(l);
						if(String.valueOf(getSubPrimaryIdValue(subPrimaryIdName, o)).equals(String.valueOf(map2.get(subForeignIdName)))) {
							subIndex=l;
							break;
						}
					}
					if(subIndex==-1) {
						if(String.valueOf(getPrimaryIdValue(primaryIdName,obj)).equals(String.valueOf(map2.get(foreignIdName)))) {
							Object o=classNewInstance(subCls);
							setObjFieldValue(o,map2,true);
							list2.add(o);
							setORMFieldValue(obj,list2);
						}
					}
					j++;
					
				}
			}
			result=list;
		}
		return result;
	}
	private Object getPrimaryIdValue(String parimayIdName,Object obj) {
		try {
			Object result=null;
			Field f = cls.getDeclaredField(parimayIdName);
			if(f!=null){
				String methodName=getMethodName(parimayIdName);
				try {
					Method m=cls.getDeclaredMethod(methodName);
					result=m.invoke(obj);
				} catch (NoSuchMethodException e) {
					f.setAccessible(true);
					result=f.get(obj);
				} catch (InvocationTargetException e) {
					f.setAccessible(true);
					result=f.get(obj);
				}
			}
			return result;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Object getSubPrimaryIdValue(String parimayIdName,Object obj) {
		try {
			Object result=null;
			Field f = subCls.getDeclaredField(parimayIdName);
			if(f!=null){
				String methodName=getMethodName(parimayIdName);
				try {
					Method m=subCls.getDeclaredMethod(methodName);
					result=m.invoke(obj);
				} catch (NoSuchMethodException e) {
					f.setAccessible(true);
					result=f.get(obj);
				} catch (InvocationTargetException e) {
					f.setAccessible(true);
					result=f.get(obj);
				}
			}
			return result;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object converType(Class<?> cls,Object value){
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
		}else{
			return value;
		}
	}
	
	private void setORMFieldValue(Object obj,Object value) {
		try {
			Field f=cls.getDeclaredField(ormFieldName);
			if(f!=null){
				String methodName=setMethodName(ormFieldName);
				Class<?> typeCls=f.getType();
				try {
					Method m=cls.getDeclaredMethod(methodName,typeCls);
					m.invoke(obj,converType(typeCls,value));
				} catch (NoSuchMethodException e) {
					f.setAccessible(true);
					f.set(obj,value);
					f.setAccessible(false);
				} catch (InvocationTargetException e) {
					f.setAccessible(true);
					f.set(obj,value);
					f.setAccessible(false);
				}
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void setObjFieldValue(Object obj,Map<String,Object> map,boolean isSub) {
		Set<Entry<String, Object>> entrySet=map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key=entry.getKey();
			Object value=entry.getValue();
			if(isSub) {
				String classField=subFields.get(key);
				if(null!=classField) {
					classField(classField,key,value,obj,isSub);
				}
			}else {
				String classField=fields.get(key);
				if(null!=classField) {
					classField(classField,key,value,obj,isSub);
				}
			}
		}
	}
	
	
	private void fieldParse(String filed) {
		String[] attrs=filed.split(ATTR_SPLIT);
		if(attrs.length<2){
			throw new SyntaxException(ormExpress);
		}
		if(attrs[0].equalsIgnoreCase(ATTR_CLASS)) {
			cls=classForName(attrs[1]);
		}else if(attrs[0].equalsIgnoreCase(ATTR_ONE_TO_ONE)) {
			setSubAttrs(1,attrs);
		}else if(attrs[0].equalsIgnoreCase(ATTR_ONE_TO_MANY)) {
			setSubAttrs(2,attrs);
		}else if(attrs[0].equalsIgnoreCase(ATTR_MANY_TO_MANY)) {
			setSubAttrs(3,attrs);
		}else {
			if(fields.size()==0) {
				primaryIdName=attrs[0];
				foreignIdName=attrs[1];
			}
			fields.put(attrs[1], attrs[0]);
		}
	}
	
	private void setSubAttrs(int type,String[] attrs) {
		this.type=type;
		ormFieldName=attrs[1];
		String subTypeORM=cedarData.getDef(attrs[2]);
		ormSubExpress=subTypeORM;
		parseSub(subTypeORM);
	}
	
	private void subfieldParse(String filed) {
		String[] attrs=filed.split(ATTR_SPLIT);
		if(attrs.length<2){
			throw new SyntaxException(ormSubExpress);
		}
		if(attrs[0].equalsIgnoreCase(ATTR_CLASS)) {
			subCls=classForName(attrs[1]);
		}else {
			if(subFields.size()==0) {
				subPrimaryIdName=attrs[0];
				subForeignIdName=attrs[1];
			}
			subFields.put(attrs[1], attrs[0]);
		}
	}

	private static String setMethodName(String key){
		String name=METHOD_SET;
		return name+key.substring(0,1).toUpperCase()+key.substring(1);
	}
	private static String getMethodName(String key){
		String name=METHOD_GET;
		return name+key.substring(0,1).toUpperCase()+key.substring(1);
	}
	
	private void classField(String classFieldName,String tableFieldName,Object value,Object obj,boolean isSub) {
		if(null==cls||(isSub&&null==subCls)) {
			return;
		}
		Class<?> currCls=null;
		if(isSub) {
			currCls=subCls;
		}else {
			currCls=cls;
		}

		try {
			Field f=currCls.getDeclaredField(classFieldName);
			if(f!=null){
				String methodName=setMethodName(classFieldName);
				Class<?> typeCls=f.getType();
				try {
					Method m=currCls.getDeclaredMethod(methodName,typeCls);
					m.invoke(obj,converType(typeCls,value));
				} catch (NoSuchMethodException e) {
					f.setAccessible(true);
					f.set(obj,value);
					f.setAccessible(false);
				} catch (InvocationTargetException e) {
					f.setAccessible(true);
					f.set(obj,value);
					f.setAccessible(false);
				}
			}
		} catch (NoSuchFieldException e) {
			if(!isOutputNoFiledError){
				e.printStackTrace();
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}
	
	private Class<?> classForName(String className) {
		try {
			Class<?> cls=Class.forName(className);
			return cls;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Object classNewInstance(Class<?> cls) {
		try {
			Object obj=cls.newInstance();
			return obj;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
