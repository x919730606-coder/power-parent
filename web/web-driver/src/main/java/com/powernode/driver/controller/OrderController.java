package com.powernode.driver.controller;

import com.powernode.common.result.Result;
import com.powernode.driver.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {

    @Resource
    private OrderService orderService;

    @Operation(summary = "查询订单状态")
    @GetMapping("/queryOrderStatus/{orderId}")
    public Result<Integer> queryOrderStatus(@PathVariable Long orderId) {

        return Result.ok(orderService.queryOrderStatus(orderId));

    }


}

