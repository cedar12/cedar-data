package cn.cedar;

import cn.cedar.dao.ADao;
import cn.cedar.data.HandleFactory;
import cn.cedar.dto.TestDto;

import java.util.List;

public class Main {
    public static void main(String[] args)  {
        // 设置jdbc 如连接池
        HandleFactory.setJdbc(new JdbcExtension());
        HandleFactory factory=new HandleFactory();
        ADao d= (ADao) factory.getInstance(ADao.class);


        long time=System.currentTimeMillis();
        List<TestDto> list=d.selectDto();
        for (TestDto dto:list) {
            System.out.println(dto);
        }
        System.out.println("耗时："+(System.currentTimeMillis() - time));
        System.out.println("===========");
        time=System.currentTimeMillis();
        System.out.println(d.count(null,null));
        System.out.println("耗时："+(System.currentTimeMillis() - time));


        // 开启事务
        HandleFactory.getJdbc().setAutoCommit(false);

        int b=d.update(1483,null,"事务成功");
        int a=d.insert(1,"事务");
        System.out.println(String.format("insert: %s, update: %s", a, b));
        if(b>0&&a>0){
            // 提交事务
            HandleFactory.getJdbc().commit();
        }else{
            // 回滚事务
            HandleFactory.getJdbc().rollback();
        }

        // 获取插入一条数据并获取其自增长id
        System.out.println(d.insertGetKey(10000, "自增长id"));

    }

}

