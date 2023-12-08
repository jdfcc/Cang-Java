package com.Cang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TestMapper.xml
 * @DateTime 2023/11/27 16:18
 */
@Mapper
public interface TestMapper{

    List<HashMap <String,Object>> selectAll();

    void createTable(@Param("name") String name);
}
