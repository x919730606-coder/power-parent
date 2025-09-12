package com.powernode.order.client;

import com.powernode.common.result.Result;
import com.powernode.model.entity.order.OrderMonitorRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(value = "service-order")
public interface OrderMonitorFeignClient {

    @PostMapping("/order/monitor/saveOrderMonitorRecord")
    Result<Boolean> saveOrderMonitorRecord(OrderMonitorRecord orderMonitorRecord);


}