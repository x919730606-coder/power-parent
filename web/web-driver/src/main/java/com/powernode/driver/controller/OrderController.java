package com.powernode.driver.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.driver.service.OrderService;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.NewOrderDataVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Operation(summary = "查询新订单队列数据")
    @GetMapping("/findNewOrderQueueData/{driverId}")
    public Result<List<NewOrderDataVo>> findNewOrderQueueData(@PathVariable Long driverId) {

        return Result.ok(orderService.findNewOrderQueueData(driverId));

    }

    @Operation(summary = "查找配送员当前订单")
    @PowerLogin
    @GetMapping("/searchDriverCurrentOrder")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrder() {

        Long userId = AuthContextHolder.getUserId();
        return Result.ok(orderService.searchDriverCurrentOrder(userId));

    }


}

