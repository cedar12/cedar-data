package cn.cedar;

import cn.cedar.dao.ADao;
import cn.cedar.data.HandleFactory;
import cn.cedar.dto.TestDto;

import java.util.List;

public class Main {
    public static void main(String[] args)  {

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

    }

}

