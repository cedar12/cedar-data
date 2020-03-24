# cedar-data
使用反射、动态代理实现mybatis部分类似功能


# 使用
Maven项目
> 依赖可在https://search.maven.org/artifact/com.github.cedar12/cedar-data/1.0.1/jar 中搜索到
```xml
<dependency>
  <groupId>com.github.cedar12</groupId>
  <artifactId>cedar-data</artifactId>
  <!-- 推荐使用最新 -->
  <version>1.0.2</version>
</dependency>
```
非Maven项目
下载target/cedar-data-版本号.jar文件引入你的项目

如何使用

新建ADao.java
```java
public interface  ADao{
    Integer insert(@Param("a") Integer a, @Param("b")String b);
    List<Map<String, Object>> select(@Param("a") Integer a);
    Integer update(@Param("id")Integer id,@Param("a") Integer a,@Param("b") String b);
    Integer del(@Param("id")Integer id);
    int count(@Param("a")Integer a,@Param("b")String b);
    List<TestDto> selectDto();
}

```
新建Main.java
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
文件内容格式
```
方法名 返回实体类(可选):{
sql主体（可使用#{}表达式）
};
```
新建ADao.java同级目录文件ADao
```
/*插入数据*/
insert:{
insert into test(id#{a!=null?',a':''}#{b!=null?',b':''}) values(null#{a!=null?','+a:''}#{b!=null?','+b:''})
};
/*查询数据返回map*/
select:{
select * from test where 1=1 #{a!=null?'and  a='+a:''}
};
/*
查询数据返回dto
*/
selectDto cn.cedar.dto.TestDto:{
select id,b from test
};
update:{
update test set  a=#{a} #{b!=null?', b='+b:''}  where id=#{id}
};
del:{
delete from test where  id=#{id}
};
count:{
SELECT count(1) from test #{a!=null||b!=null?'where 1=1':''} #{a!=null?' and a='+a:''}  #{b!=null?' and b like'+b:''}
};
```