package com.powernode.order.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.form.order.StartDriveForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
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
    @PostMapping(value = "/robNewOrder/{orderId}/{driverId}")
    public Result<Boolean> robNewOrder(@PathVariable Long orderId,@PathVariable Long driverId) {

        Boolean result = orderInfoService.robNewOrder(driverId, orderId);
        return Result.ok(result);

    }

    @Operation(summary = "查询顾客当前订单信息")
    @GetMapping(value = "/searchCustomerCurrentOrder/{userId}")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrderInfo(@PathVariable Long userId) {

        CurrentOrderInfoVo currentOrderInfoVo = orderInfoService.searchCustomerCurrentOrderInfo(userId);
        return Result.ok(currentOrderInfoVo);

    }

    @Operation(summary = "查询司机当前订单信息")
    @GetMapping(value = "/searchDriverCurrentOrder/{driverId}")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrderInfo(@PathVariable Long driverId) {

        CurrentOrderInfoVo currentOrderInfoVo = orderInfoService.searchDriverCurrentOrderInfo(driverId);
        return Result.ok(currentOrderInfoVo);

    }

    @Operation(summary = "根据id查询订单信息")
    @GetMapping(value = "/getOrderInfo/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable Long orderId) {

        OrderInfo orderInfo = orderInfoService.getById(orderId);
        return Result.ok(orderInfo);

    }

    @Operation(summary = "配送员到达开始位置")
    @PostMapping(value = "/driverArrivedStartLocation/{orderId}/{driverId}")
    public Result<Boolean> driverArrivedStartLocation(@PathVariable Long orderId,@PathVariable Long driverId) {

        Boolean result = orderInfoService.driverArrivedStartLocation(orderId, driverId);
        return Result.ok(result);

    }

    @Operation(summary = "更新订单车辆信息")
    @PostMapping(value = "/updateOrderCart")
    public Result<Boolean> updateOrderCart(@RequestBody UpdateOrderCartForm orderCartForm) {

        return Result.ok(orderInfoService.updateOrderCart(orderCartForm));

    }

    @Operation(summary = "配送员开始配送")
    @PostMapping(value = "/startDrive")
    public Result<Boolean> startDrive(@RequestBody StartDriveForm startDriveForm) {

        return Result.ok(orderInfoService.startDrive(startDriveForm));

    }

    @Operation(summary = "查询订单数量")
    @GetMapping(value = "/getOrderByTime/{startTime}/{endTime}")
    public Result<Long> getOrderNumByTime(@PathVariable String startTime,@PathVariable String endTime) {

        Long orderCount = orderInfoService.getOrderNumByTime(startTime, endTime);
        return Result.ok(orderCount);

    }
}

