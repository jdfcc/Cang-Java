package com.Cang.mapper;

import com.Cang.dto.GameDetailDto;
import com.Cang.entity.Game;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameMapper
 * @DateTime 2024/1/10 16:58
 */
public interface GameMapper extends BaseMapper<Game> {

    List<String> getTags();

    List<Game> getByTag(@Param("tag") String tag,
                        @Param("index") Integer index,
                        @Param("pageSize") Integer pageSize);

    GameDetailDto getDetail(@Param("id") String id);
}
