package com.powernode.order.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.order.service.OrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@Tag(name = "订单API接口管理")
@RestController
@RequestMapping(value="/order/info")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderInfoController {
	
    @Resource
    private OrderInfoService orderInfoService;

    @Operation(summary = "添加订单")
    @PostMapping(value = "/addOrderInfo")
    public Result<Long> addOrderInfo(@RequestBody OrderInfoForm orderInfoForm) {

        Long orderId = orderInfoService.addOrderInfo(orderInfoForm);
        return Result.ok(orderId);

    }

    @Operation(summary = "查询订单状态")
    @GetMapping(value = "/queryOrderStatus/{orderId}")
    public Result<Integer> queryOrderStatus(@PathVariable Long orderId) {

        Integer orderStatus = orderInfoService.queryOrderStatus(orderId);
        return Result.ok(orderStatus);

    }

    @Operation(summary = "抢单")
    @PostMapping(value = "/robNewOrder/{orderId}")
    @PowerLogin
    public Result<Boolean> robNewOrder(@PathVariable Long orderId) {

        Long driverId = AuthContextHolder.getUserId();
        Boolean result = orderInfoService.robNewOrder(driverId, orderId);
        return Result.ok(result);

    }

    @Operation(summary = "查询当前用户的订单信息")
    @GetMapping(value = "/searchCustomerCurrentOrder/{userId}")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrderInfo(@PathVariable Long userId) {

        CurrentOrderInfoVo currentOrderInfoVo = orderInfoService.searchCustomerCurrentOrderInfo(userId);
        return Result.ok(currentOrderInfoVo);

    }
}

