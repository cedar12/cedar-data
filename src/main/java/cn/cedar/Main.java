package cn.cedar;

import cn.cedar.dao.ADao;
import cn.cedar.data.HandleFactory;
import cn.cedar.data.JdbcUtil;

public class Main {

    public static void p(Object obj){
        System.out.println(obj);
    }

    public static void main(String[] args)  {
        HandleFactory factory=new HandleFactory();
        ADao d= (ADao) factory.getInstance(ADao.class);
        long time=System.currentTimeMillis();
        d.selectDto().forEach(Main::p);
        System.out.println("耗时："+(System.currentTimeMillis() - time));
        System.out.println("===========");
        time=System.currentTimeMillis();
        d.select(null).forEach(System.out::println);
        System.out.println("耗时："+(System.currentTimeMillis() - time));

    }

}

