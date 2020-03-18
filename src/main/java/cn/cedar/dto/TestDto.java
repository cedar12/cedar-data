package cn.cedar.dto;

public class TestDto {

    private Integer id;
    private Integer a;
    private String b;

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

    @Override
    public String toString() {
        return "TestDto{" +
                "id=" + id +
                ", a=" + a +
                ", b='" + b + '\'' +
                '}';
    }
}
