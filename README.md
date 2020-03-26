# cedar-data
简单易用持久层框架（基于jdbc）

![](https://img.shields.io/github/issues/cedar12/cedar-data.svg)
![](https://img.shields.io/badge/jdk-1.7%2B-brightgreen.svg)
![](https://img.shields.io/github/forks/cedar12/cedar-data.svg)
![](https://img.shields.io/github/stars/cedar12/cedar-data.svg)
![](https://img.shields.io/github/license/cedar12/cedar-data.svg)
![](https://img.shields.io/badge/maven-com.github.cedar12-green.svg)
![](https://img.shields.io/badge/language-java-green.svg)


# 使用
Maven项目
> 可在Maven中央仓库中搜索到
```xml
<dependency>
  <groupId>com.github.cedar12</groupId>
  <artifactId>cedar-data</artifactId>
  <!-- 推荐使用最新 -->
  <version>版本号</version>
</dependency>
```
非Maven项目
cedar-data-版本号.jar文件引入你的项目

如何使用

在项目根目录创建jdbc.properties文件，内容如下
```properties
#以mysql为例
url=jdbc:mysql://127.0.0.1:3306/test
#数据库用户
user=root
#数据库密码
password=**
#驱动类全路径
driverClass=com.mysql.jdbc.Driver
```

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


默认使用DriverManager.getConnection()获取连接，如需使用连接池，使用如下（已druid连接池为例）
1.继承cn.cedar.JdbcManager
2.重写init()、getConnection()方法

```java
public class DruidDataSourceManager extends JdbcManager {

    /**
     * 初始化连接池
     * @return
     */
    @Override
    public DataSource init(){
        DruidDataSource datasource=new DruidDataSource();
        datasource.setUrl(getUrl());
        datasource.setUsername(getUser());
        datasource.setPassword(getPassword());
        datasource.setDriverClassName(getDriverClass());
        return datasource;
    }

    /**
     * 获取连接
     * @return
     */
    @Override
    public Connection getConnection() {
        try {
            // dataSource为JdbcManager成员变量
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```


新建Main.java
```java
public class Main{
    public static void main(String[] args){
        /*
        1.1.x+ 版本 的实例获取
        */
        ADao d=InstanceFactory.getInstance(ADao.class);
        // 使用druid连接池
        InstanceFactory.setJdbcManager(new DruidDataSourceManager());
        
        // 是否将sql打印到控制台，默认false
        InstanceFactory.setDisplaySql(true);
        /*
        1.0.x 版本 的实例获取
        */
        // 实例工厂
        HandleFactory factory=new HandleFactory();
        // 获取ADao的实例
        ADao d= (ADao) factory.getInstance(ADao.class);

        JdbcManager manager=InstanceFactory.getJdbcManager();
        // 关闭自动提交，开始事务
        manager.setAutoCommit(false);
        d.insert(1,'张三');
        // 回滚事务
        manager.rollback();
        // 提交事务
        manager.commit();
    }
}
```

方法sql文件

文件内容格式
```
/*这是注释，不会被解析*/
方法名 全类名(方法返回类型是List<类>的必填，其它可不填):{
sql主体（可使用#[]表达式输出值）
};
```
注意：

1.文件名及路径必需对应类

2.方法名需对象类中的方法

3.实体类返回类型必需全类名

4.一条方法sql必需已;符号结尾



新建ADao.java同级目录文件ADao
```
/*插入数据*/
insert:{
insert into test(id#[a!=null?',a':'']#[b!=null?',b':'']) values(null#[a!=null?','+a:'']#[b!=null?','+b:''])
};
/*查询数据返回map*/
select:{
select * from test where 1=1 #[a!=null?'and  a='+a:'']
};
/*
查询数据返回dto
*/
selectDto cn.cedar.dto.TestDto:{
select id,b from test
};
update:{
update test set  a=#[a] #[b!=null?', b='+b:'']  where id=#[id]
};
/*
1.1.2开始新增args方式获取参数值 args是数组类型  下标对应接口类中参数顺序从0开始。该获取参数值方式可不使用@Param()注解
*/
del:{
delete from test where  id=#[args[0]]
};
count:{
SELECT count(1) from test #[a!=null||b!=null?'where 1=1':''] #[a!=null?' and a='+a:'']  #[b!=null?' and b like'+b:'']
};
```
