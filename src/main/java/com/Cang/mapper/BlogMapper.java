package com.Cang.mapper;

import com.Cang.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jdfcc
 * @since 2023-2-7
 */
public interface BlogMapper extends BaseMapper<Blog> {

    @Update("update tb_blog set liked = liked +1 where id=#{id}")
    Boolean liked(@Param("id") Long id);

    @Update("update tb_blog set liked = liked -1 where id=#{id}")
    Boolean unliked(@Param("id") Long id);

    @Select("select id from tb_blog where user_id=#{user_id};")
    List<String> getIds(@Param("user_id") String user_id);
}
