package com.Cang.service.impl;

import com.Cang.entity.Game;
import com.Cang.mapper.GameMapper;
import com.Cang.service.GameService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameServiceImpl
 * @DateTime 2024/1/10 16:59
 */
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper, Game> implements GameService {
}
