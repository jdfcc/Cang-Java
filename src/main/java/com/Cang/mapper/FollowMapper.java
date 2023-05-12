package com.Cang.mapper;

import com.Cang.entity.Follow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author jdfcc
 * @since 2023-2-11
 */
public interface FollowMapper extends BaseMapper<Follow> {

    @Select("select user_id from tb_follow where follow_user_id=#{follow_user_id}")
    List<String> getFollowerId(@Param("follow_user_id") Long follow_user_id);

}
