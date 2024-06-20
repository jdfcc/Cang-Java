package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.entity.GameOrder;
import com.Cang.service.AliService;
import com.Cang.utils.IdGeneratorSnowflake;
import com.Cang.utils.UserHolder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description AliPayController
 * @DateTime 2024/4/7 16:00
 */
@RestController
@Slf4j
@RequestMapping("/ali")
public class AliPayController {

    @Resource
    AliService aliService;
//    @Resource
//    GameOrderService gameOrderService;
    @Resource
    IdGeneratorSnowflake idGeneratorSnowflake;


    @PostMapping("/pay")
    public void pay(@RequestBody GameOrder order, HttpServletResponse httpResponse) throws Exception {
        Long user = UserHolder.getUser();
        long orderNo = idGeneratorSnowflake.snowflakeId();
        String form;
        form = aliService.createPaymentForm(String.valueOf(orderNo), order.getTotalAmount(), order.getSubject(), user);
        httpResponse.setContentType("text/html;charset=UTF-8");
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @PostMapping("/notify")
    public Result payNotify(HttpServletRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();

        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }
        if ("TRADE_SUCCESS".equals(params.get("trade_status"))) {
            if (aliService.verifyAndProcessNotification(params)) {
//                TODO 修改订单状态并返回一个该游戏的cdk
//                gameOrderService.paysuccess(params.get("trade_id"));
//               String cdk= gameService.getGameCdk(params.get("game_id"));
               return Result.ok("cdk");
            }
        }
        return Result.fail("用户未支付");
    }
}