package cn.cedar.data.struct;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Type{

    public static final int ONE_TO_ONE=1;
    public static final int ONE_TO_MANY=2;
    public static final int MANY_TO_MANY=3;

    private String target;
    private Class<?> targetClass;
    private String primaryKeyName;
    private String primaryFieldName;
    private String firstFieldName;
    private String secondFieldName;
    private String relationKeyName;
    private int type;
    private List<String> keyNames=new ArrayList<>();
    private List<String> fieldNames=new ArrayList<>();
    private Map<String,String> names=new HashMap<>();
    private List<Type> types=new ArrayList<>();
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        try {
            setTargetClass(Class.forName(target));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.target = target;
    }
    public Class<?> getTargetClass() {
        return targetClass;
    }
    public Object getTargetInstance() {
        try {
            return this.targetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public List<String> getKeyNames() {
        return keyNames;
    }
    public void setKeyNames(List<String> keyNames) {
        this.keyNames = keyNames;
    }
    public List<String> getFieldNames() {
        return fieldNames;
    }
    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }
    public String getPrimaryKeyName() {
        return primaryKeyName;
    }
    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }
    public String getPrimaryFieldName() {
        return primaryFieldName;
    }
    public void setPrimaryFieldName(String primaryFieldName) {
        this.primaryFieldName = primaryFieldName;
    }
    public Map<String, String> getNames() {
        return names;
    }
    public void put(String key,String value) {
        if(this.keyNames.size()==0) {
            setPrimaryKeyName(key.trim());
            setPrimaryFieldName(value.trim());
        }
        this.keyNames.add(key.trim());
        this.fieldNames.add(value.trim());
    }
    public void setNames(Map<String, String> names) {
        this.names = names;
    }
    public List<Type> getTypes() {
        return types;
    }
    public void setTypes(List<Type> types) {
        this.types = types;
    }
    public void add(Type type) {
        this.types.add(type);
    }
    public String getFirstFieldName() {
        return firstFieldName;
    }
    public void setFirstFieldName(String firstFieldName) {
        this.firstFieldName = firstFieldName;
    }
    public String getSecondFieldName() {
        return secondFieldName;
    }
    public void setSecondFieldName(String secondFieldName) {
        this.secondFieldName = secondFieldName;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getRelationKeyName() {
        return relationKeyName;
    }
    public void setRelationKeyName(String relationKeyName) {
        this.relationKeyName = relationKeyName;
    }

}


