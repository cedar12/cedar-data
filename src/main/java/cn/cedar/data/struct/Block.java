package cn.cedar.data.struct;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cedar12.zxd@qq.com
 */
public class Block {

    private String name;

    private String type;

    private String body;

    private Class<?> target;

    private int startLineNum;

    private int endLineNum;

    private List<String> express=new ArrayList<>();

    private String sql;

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getBody() {
        return body;
    }


    public void setBody(String body) {
        this.body = body;
    }


    public int getStartLineNum() {
        return startLineNum;
    }


    public void setStartLineNum(int startLineNum) {
        this.startLineNum = startLineNum;
    }


    public int getEndLineNum() {
        return endLineNum;
    }


    public void setEndLineNum(int endLineNum) {
        this.endLineNum = endLineNum;
    }


    public List<String> getExpress() {
        return express;
    }

    public void setExpress(List<String> express) {
        this.express = express;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Block [name=" + name + ", type=" + type + ", body=" + body + "]";
    }


    @Override
    public int hashCode() {
        return (this.name + this.type).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Block) {
            Block b = (Block) obj;
            if (this.getName().equals(b.getName()) && this.getType().equals(b.getType())) {
                return true;
            }
        }
        return false;
    }


}
