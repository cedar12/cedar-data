package cn.cedar.dto;


import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Date;

public class TestDto {

    private Integer id;
    private Integer a;
    private String b;
    private Date c;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public Date getC() {
        return c;
    }

    public void setC(Date c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "TestDto{" +
                "id=" + id +
                ", a=" + a +
                ", b='" + b + '\'' +
                ", c=" + c +
                '}';
    }

}
