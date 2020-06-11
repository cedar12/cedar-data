# Cedar Data 数据持久层框架
- 环境要求：`jdk1.7+`
- 本文档适用于：`1.3.2+`

![](https://img.shields.io/github/issues/cedar12/cedar-data.svg)
![](https://img.shields.io/badge/jdk-1.7%2B-brightgreen.svg)
![](https://img.shields.io/github/forks/cedar12/cedar-data.svg)
![](https://img.shields.io/github/stars/cedar12/cedar-data.svg)
![](https://img.shields.io/github/license/cedar12/cedar-data.svg)
![](https://img.shields.io/badge/maven-com.github.cedar12-green.svg)
![](https://img.shields.io/badge/language-java-green.svg)


旧文档请移至[README_old.md](https://github.com/cedar12/cedar-data/blob/master/README_old.md)

## Maven
```xml
<dependency>
  <groupId>com.github.cedar12</groupId>
  <artifactId>cedar-data</artifactId>
  <version>1.3.2</version>
</dependency>
```

## Gradle
```groovy
compile group: 'com.github.cedar12', name: 'cedar-data', version: '1.3.2'
```

## 特点
1. 体积小
2. 无第三方依赖
3. 简单易使用
4. 灵活
5. 可`sql`和`java`代码分离
6. 支持动态`sql`
7. 字段映射
8. 只需定义`interface`无需实现


## SQL配置文件
- 文件后缀：`.cd`（例如`A.cd`）
- 文件位置：`classpath`目录为根目录

### 语法格式

#### 常量
使用`def`关键字定义一个常量名`table`值为`test`的常量如下：
```text
def table=test;
```
如值的前后需要空格可用""
```text
def table="  test  ";
```
> 注：只能写在一行并已`;`结束
#### 取值
定义queryTableTest并使用常量table，使用`#{}`表达式取常量`table`的值如下：
```text
def queryTableTest=select * from #{table};
```


#### sql体
定义一个id为`query`的sql体，返回结果为`List<Map<String,Object>`如下：
```text
query:{
    select * from test
};
```
返回结果为`List<实体类>`如下：
```text
query 实体类全路径:{
    select * from test
};
```
insert语句返回自增长id（需数据库支持），使用`key`如下：

```text
add key:{
    insert into test values(null,now())
};
```

> 注：必需以`;`结束

### 动态sql
- 支持逻辑运算符、关系运算符
- 只能在sql体内使用
- 不支持嵌套

```text
/*
#if 条件:
 条件真 
#end

#if 条件:
 条件真 
#else 
 条件假 
#end

#if 条件1:
 条件1真 
#elif 条件2:
 条件2真
#else
 条件1、条件2都为假
#end
*/
query:{
    select * from test 
    #if $id!=null:
        where
    #end
    #if ?id!=null:
        id=$id
    end
};
```


### 导入其它配置
可导入其它配置文件非private修饰的常量定义、sql体以及import配置。使用`import`关键字如下
```text
import a;
```
以下常量和sql体不可被导入
```text
private def table=test;
private query:{
    select * from test
};
```

### 注释
- // 单行注释（sql体内不可用）
- /* 多行注释 */


### 上手例子

新建实体类如下
```java
package test.cat;

public class Cat{
    private Integer id;
    private String name;
    public Cat() {}
    public Cat(Integer id, String name) {
    	this.id = id;
    	this.name = name;
    }
    //getter setter setter不写会直接注入到字段，优先注入setter
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Cat [id=" + id + ", name=" + name + "]";
    }
    
}
```

新建Dao接口如下
```java
/*
cd 指定配置文件位置，从classpath目录开始
*/
package test.cat;

import java.util.List;

import cn.cedar.data.annotation.CedarData;
import cn.cedar.data.annotation.Param;

@CedarData(cd="CatDao")
public interface CatDao{
    
    List<Cat> findAll();
    
    Cat findById(@Param("id")Integer id);
    
    int add(@Param("cat")Cat cat);
    
    boolean modify(@Param("cat")Cat cat);
    
}
```
新建配置CatDao.cd如下
```text

def cat_class="test.cat.Cat";

findAll #{cat_class}:{
    select *  from cat
};
/* 
$id 名称取参数值 取CatDao findById方法的参数值 id对应@Param()内参数
*/
findById #{cat_class}:{
    select * from cat where id=$id
};
/* 
?1 占位取参数值 取CatDao add方法的参数值 1对应方法第一个参数
*/
add key:{
    insert into cat values( ?1.id , $cat.name )
};

modify:{
	update cat set name=$cat.name where id=?1.id
};

```

新建Test如下
```java
package test.cat;

import java.util.List;

import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;

import cn.cedar.data.InstanceFactory;
import cn.cedar.data.JdbcManager;

public class CatTest {
	// 获取实现类
	private static CatDao catDao = InstanceFactory.getInstance(CatDao.class);
	@Test
	public void test() {
        // 设置数据源, 这里以使用druid数据源连接mysql为例
        DruidDataSource druidDataSource=new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test");
        druidDataSource.setUsername("用户");
        druidDataSource.setPassword("密码");
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        JdbcManager.setDataSource(druidDataSource);
        
        // 新增数据
        Cat garfield=new Cat();
        garfield.setId(null);
        garfield.setName("加菲猫5");
        int id=catDao.add(garfield);
        System.out.println(id);
        
        // 修改刚新增的数据
        garfield.setId(99);
        garfield.setName("我是被修改了");
        boolean result=catDao.modify(garfield);
        System.out.println(result);

        // 调用dao接口获取全部数据
        List<Cat> cats=catDao.findAll();
        System.out.println(cats);

        // 查询指定id的数据
        Cat cat=catDao.findById(1);
        System.out.println(cat);

	}
}
```
上手例子结束


## 让配置文件有颜色和提示
### 安装方法 
将`cedar-data-file-syntax-highlight-for-eclipse_1.0.0.jar`文件复制到`eclipse根目录/plugins/`目录下，重启eclipse即可

![image](https://github.com/cedar12/cedar-data/blob/master/image/%E6%8F%92%E4%BB%B6%E6%95%88%E6%9E%9C%E5%9B%BE.jpg)




