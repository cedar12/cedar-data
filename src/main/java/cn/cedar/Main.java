package cn.cedar;

import cn.cedar.dao.ADao;
import cn.cedar.data.HandleFactory;

public class Main {



    public static void main(String[] args)  {
        HandleFactory<ADao> factory=new HandleFactory();
        ADao d=factory.getInstance(ADao.class);
        long time=System.currentTimeMillis();
        /*
        for(int i=100;i<=1000;i++){
            System.out.println(i+">>>"+d.insert(i, "2测试插入"+i));
        }

        */
        d.select(null).forEach(System.out::println);
        System.out.println("耗时："+(System.currentTimeMillis() - time));
        //Integer a=d.insert(22,"渣渣伟狗子");
        /*
        System.out.println(a);
        System.out.println(d.xiugai(2, 11111, "渣渣伟狗子被修改"));
        d.select(1).forEach(System.out::println);
        System.out.println(d.del(11));
        System.out.println(d.count(null, null));
        */

    }

}

