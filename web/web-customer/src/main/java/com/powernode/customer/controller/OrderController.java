package com.powernode.customer.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.customer.service.OrderService;
import com.powernode.model.form.customer.ExpectOrderForm;
import com.powernode.model.form.customer.SubmitOrderForm;
import com.powernode.model.vo.customer.ExpectOrderVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {

    @Resource
    private OrderService orderService;

    @Operation(summary = "查询当前进行中订单")
    @GetMapping("/searchCustomerCurrentOrder/")
    @PowerLogin
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder(){

        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(orderService.searchCustomerCurrentOrderInfo(customerId));

    }

    @Operation(summary = "估算订单价格")
    @PostMapping("/expectOrder")
    public Result<ExpectOrderVo> expectOrder(@RequestBody ExpectOrderForm expectOrderForm){

        return Result.ok(orderService.expectOrder(expectOrderForm));

    }

    @Operation(summary = "添加订单")
    @PostMapping("/submitOrder")
    @PowerLogin
    public Result<Long> addOrder(@RequestBody SubmitOrderForm submitOrderForm){

        submitOrderForm.setCustomerId(AuthContextHolder.getUserId());
        return Result.ok(orderService.addOrder(submitOrderForm));

    }

    @Operation(summary = "查询订单状态")
    @GetMapping("/queryOrderStatus/{orderId}")
    public Result<Integer> queryOrderStatus(@PathVariable Long orderId){

        return Result.ok(orderService.queryOrderStatus(orderId));

    }



}

