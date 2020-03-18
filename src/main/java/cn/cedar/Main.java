package cn.cedar;

import cn.cedar.dao.ADao;
import cn.cedar.data.HandleFactory;
import cn.cedar.data.JdbcUtil;

public class Main {



    public static void main(String[] args)  {
        JdbcUtil.register();
        HandleFactory factory=new HandleFactory();
        ADao d= (ADao) factory.getInstance(ADao.class);
        long time=System.currentTimeMillis();
        d.selectDto().forEach(System.out::println);
        System.out.println("耗时："+(System.currentTimeMillis() - time));
        System.out.println("===========");
        time=System.currentTimeMillis();
        d.select(null).forEach(System.out::println);
        System.out.println("耗时："+(System.currentTimeMillis() - time));

    }

}

