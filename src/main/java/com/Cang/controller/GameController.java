package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.entity.Game;
import com.Cang.service.GameService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameResource
 * @DateTime 2024/1/10 16:56
 */
@RestController
@RequestMapping("/game")
public class GameController {

    @Resource
    private GameService gameService;

    @PostMapping("/add")
    public Result addGame(@RequestBody Game game) {
        gameService.save(game);
        return Result.ok();
    }
}
