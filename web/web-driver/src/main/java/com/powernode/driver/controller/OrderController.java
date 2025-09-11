package com.powernode.driver.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.driver.service.OrderService;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.vo.map.DrivingLineVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.NewOrderDataVo;
import com.powernode.model.vo.order.OrderInfoVo;
import com.powernode.order.client.OrderInfoFeignClient;
import com.powernode.order.service.OrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {

    @Resource
    private OrderService orderService;
    @Resource
    private OrderInfoService orderInfoService;


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

    @Operation(summary = "抢单")
    @PowerLogin
    @GetMapping("/robNewOrder/{orderId}")
    public Result<Boolean> robNewOrder(@PathVariable Long orderId) {

        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderInfoService.robNewOrder(driverId, orderId));

    }

    @Operation(summary = "配送员查看当前订单信息")
    @PowerLogin
    @GetMapping("/getOrderInfo/{orderId}")
    public Result<OrderInfoVo> getOrderInfo(@PathVariable Long orderId) {

        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.getOrderInfo(driverId, orderId));

    }

    @Operation(summary = "配送员去顾客位置起始路线")
    @PowerLogin
    @GetMapping("/calculateDrivingLine")
    public Result<DrivingLineVo> calculateDrivingLine(@RequestBody CalculateDrivingLineForm drivingLineForm) {

        return Result.ok(orderService.calculateDrivingLine(drivingLineForm));

    }


}

