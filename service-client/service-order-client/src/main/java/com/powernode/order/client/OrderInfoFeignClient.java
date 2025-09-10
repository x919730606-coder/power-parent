package com.powernode.order.client;

import com.powernode.common.result.Result;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "service-order")
public interface OrderInfoFeignClient {

    @PostMapping(value = "/order/info/addOrderInfo")
    Result<Long> addOrderInfo(@RequestBody OrderInfoForm orderInfoForm);

    @GetMapping(value = "/order/info/queryOrderStatus/{orderId}")
    Result<Integer> queryOrderStatus(@PathVariable Long orderId);

    @GetMapping(value = "/order/info/searchCustomerCurrentOrder/{userId}")
    Result<CurrentOrderInfoVo> searchCustomerCurrentOrderInfo(@PathVariable Long userId);

}