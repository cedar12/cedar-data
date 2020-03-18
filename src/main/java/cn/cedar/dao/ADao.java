package cn.cedar.dao;

import cn.cedar.data.Param;
import cn.cedar.dto.TestDto;

import java.util.List;
import java.util.Map;

public interface  ADao{
    Integer insert(@Param("a") Integer a, @Param("b")String b);
    List<Map<String, Object>> select(@Param("a") Integer a);
    Integer update(@Param("id")Integer id,@Param("a") Integer a,@Param("b") String b);
    Integer del(@Param("id")Integer id);
    Integer count(@Param("a")Integer a,@Param("b")String b);
    List<TestDto> selectDto();
}
