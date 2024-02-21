package com.Cang.controller;


import com.Cang.dto.Result;
import com.Cang.entity.Game;
import com.Cang.service.GameService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

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

    private static final int PAGE_SIZE = 20;
    @Resource
    private GameService gameService;

    @PostMapping("/add")
    public Result addGame(@RequestBody Game game) {
        gameService.save(game);
        return Result.ok();
    }

    @GetMapping("/list")
    public Result listGame(@RequestParam(value = "index", defaultValue = "1") Integer index) {
        Page<Game> page = gameService.query()
                .page(new Page<>(index, PAGE_SIZE));
        return Result.ok(page);
    }
}
