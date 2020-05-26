# cedar-data
简单易用持久层框架（基于jdbc）

![](https://img.shields.io/github/issues/cedar12/cedar-data.svg)
![](https://img.shields.io/badge/jdk-1.7%2B-brightgreen.svg)
![](https://img.shields.io/github/forks/cedar12/cedar-data.svg)
![](https://img.shields.io/github/stars/cedar12/cedar-data.svg)
![](https://img.shields.io/github/license/cedar12/cedar-data.svg)
![](https://img.shields.io/badge/maven-com.github.cedar12-green.svg)
![](https://img.shields.io/badge/language-java-green.svg)


集成Spring详情见 [cedar-data-spring](https://github.com/cedar12/cedar-data-spring.git)

集成Spring Boot详情见 [cedar-data-spring-boot-starter](https://github.com/cedar12/cedar-data-spring-boot-starter.git)


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

## 新建ADao.java
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


## 新建Main.java
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

## 方法sql文件

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

4.一条方法sql必需已`;`符号结尾

## `#[]`表达式
`#[]`表达式含js特性

`#[]`表达式内不能`}`符号后接`;`符号

`#[]`表达式内字符变量用单引号
### 变量
```javascript
// 变量名不能是args和方法参数@Param注解参数值
var a=123;
```
### 条件语句-if
```javascript
/*---示例if语句---*/
if(判断语句：关系表达式或逻辑表达式){
	语句1
}else{
	语句2
}
	语句3;
/*--- else if语句 ---*/
if (判断语句1) {
	语句1
}
else if (判断语句2) {
	语句2
}
else {
	语句3
}
```
### 多分支语句—— switch
```javascript
switch(条件判断语句){
   case 匹配值1:语句1;break;
   case 匹配值2:语句2;break;
   …………
   default：语句块n;break;
   /*break关键字会导致代码执行流跳出switch语句，
   如果省略break关键字，就会导致执行完当前case后，继续执行下一个case。*/
}
```

### 循环语句—— for
```javascript
for(表达式1（初始值）;判断表达式2（条件判断）;表达式3（变量运算){
	语句a;
	语句b;
	语句c;
	语句……;
}
for(变量 in 变量){
	语句a;
	语句b;
	语句c;
	语句……;
}

```

### while 循环语句
```javascript
while(判断语句){
    循环体;
}
```

### 数组

| 属性 | 说明 | 
| ---- | ------ |
| length | 设置或返回数组中元素的数目。| 

|方法|说明|
|---- |----|
concat()|连接两个或更多的数组，并返回结果。
join()|把数组的所有元素放入一个字符串。元素通过指定的分隔符进行分隔。
pop()	|删除并返回数组的最后一个元素
push()	|向数组的末尾添加一个或更多元素，并返回新的长度。
reverse()	|颠倒数组中元素的顺序。
shift()	|删除并返回数组的第一个元素
slice()	|从某个已有的数组返回选定的元素
sort()	|对数组的元素进行排序
splice()	|删除元素，并向数组添加新元素。
toSource()	|返回该对象的源代码。
toString()	|把数组转换为字符串，并返回结果。
toLocaleString()	|把数组转换为本地数组，并返回结果。
unshift()	|向数组的开头添加一个或更多元素，并返回新的长度。
valueOf()	|返回数组对象的原始值 


新建ADao.java同级目录文件ADao.cd
```
/*导入另一cd文件内容*/
import 文件;

/*插入数据*/
insert /*此次填写key则会返回插入数据库中的自增长id（需数据库支持）*/:{
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
/*1.2.0 新增语法*/
def a="user_id as userId";
selectDef:{
select ${a} from test where 1=1 #[a!=null?'and  a='+a:'']
/*等同 select user_id as userId  from test where 1=1 #[a!=null?'and  a='+a:'']*/
}
```


## 版本
#### 1.1.6
##### 新增
动态方法sql文件新增import 文件路径;导入文件。导入不得超过指定层数，默认层数5
`#[]`表达式Date对象新增format方法格式化日期 yyyy-MM-dd
`#[]`表达式字符串新增to方法将sql字符转js字符，如果试js字符则转sql字符


#### 1.1.7
##### 兼容
cedar-data-spring-boot-starter


#### 1.1.8
##### 兼容
cedar-data-spring-boot-starter

#### 1.2.0
##### 新增
1.  动态sql文件将更名Cedar Data文件（简称cd文件）并以.cd为文件后缀
    - Dao -> Dao.cd
2.  支持private关键词，被private修饰的不会被import
    - private del:{delete from test};
2.  cd文件支持定义常量
    - def a="user_id id"; 可被import
    - private def a="user_id id"; 不可被import
    - 使用#{a}表达式使用常量a
3.  实体类字段值注入优先使用set方法注入，次之字段直接注入

#### 1.3.0
##### 新增
1.  注解@Def、@Query
    - @CedarData(annotation=true) 开启注解方式
    - 方法上使用@Query(value="sql语句（可同.cd文件{};内的sql写法）")
    - 方法或字段上使用@Def(name="指定def名(不指定默认方法名或字段名)",value="def的值")
2.  \#if、#elif、#else语句
    - \#if 1==1:a=1 #end
    - \#if 1==1:a=1 #else a=2 #end
    - \#if 1==1:a=1 #elif 1==2:a=2 #else a==3 #end
2.  占位符取值
    - ?取值：?参数位置（从1开始）
    - $取值：$参数名
    - 防止sql注入


## 后期计划
1.  [x] 动态sql文件将更名Cedar Data文件（简称暂定cd文件）并暂定以.cd为文件后缀
    - Dao -> Dao.cd
2.  [x] 支持private关键词，被private修饰的不会被import
    - private del:{delete from test};
2.  [x] cd文件支持定义常量
    - def a="user_id id"; 可被import
    - private def a="user_id id"; 不可被import
    - 使用#{a}表达式取a的值
3.  [x] 实体类字段值注入优先使用set方法注入，次之字段注入
4.  [x] 多文件import的def定义优先级 bata.1.3.0版本实现
    - 后一行def覆盖前一行
5.  [ ] cd文件支持定义类字段映射 beta.1.3.0版本实现（不完善）
    - def user=class:cn.cedar.data.User,id:user_id,name:user_name;
    - oo 一对一 def user=class:cn.cedar.data.User,id:user_id,name:user_name,oo:auths:auth;
    - om 一对多 def user=class:cn.cedar.data.User,id:user_id,name:user_name,om:auths:auth;
    - mm 多对多 def user=class:cn.cedar.data.User,id:user_id,name:user_name,mm:auths:auth;
6.  [x] 支持注解@Def、@Query
7.  [x] 支持#if、#elif、#else语句
    - #if 条件:sql语句 #end
8.  [x] 支持占位符?参数位置（从1开始）、$参数名
    - 使用该方式取值可防止sql注入


## beta 计划
#### beta.1.3.0
.cd文件解析重构
