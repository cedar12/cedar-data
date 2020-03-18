# cedar-data
使用反射、动态代理实现mybatis部分类似功能


# 使用
现有类
```java
public interface  ADao{
    Integer insert(@Param("a") Integer a, @Param("b")String b);
    List<Map<String, Object>> select(@Param("a") Integer a);
    Integer update(@Param("id")Integer id,@Param("a") Integer a,@Param("b") String b);
    Integer del(@Param("id")Integer id);
    Integer count(@Param("a")Integer a,@Param("b")String b);
    List<TestDto> selectDto();
}

```
```java
public class Main{
    public static void main(String[] args){
        // 实例工厂
        HandleFactory factory=new HandleFactory();
        // 获取ADao的实例
        ADao d= (ADao) factory.getInstance(ADao.class);
        d.insert(1,'张三');
    }
}
```
动态sql文件
>注：动态sql文件所在位置需要和类文件位置对应
```
/*插入数据*/
insert java.lang.Integer: {
insert into test(id#{a!=null?',a':''}#{b!=null?',b':''}) values(null#{a!=null?','+a:''}#{b!=null?','+b:''})
};
/*查询数据返回map*/
  select Map  :{
select * from test where 1=1 #{a!=null?'and  a='+a:''}
};
/*
查询数据返回dto
*/
  selectDto cn.cedar.dto.TestDto  :{
select id,b from test
};
update java.lang.Integer:{
update test set  a=#{a} #{b!=null?' and b='+b:''}  where id=#{id}
};
del java.lang.Integer:{
delete from test where  id=#{id}
};
count java.lang.Integer:{
select count(1) from test #{a!=null||b!=null?'where 1=1':''} #{a!=null?' and a='+a:''}  #{b!=null?' and b='+b:''}
};
```