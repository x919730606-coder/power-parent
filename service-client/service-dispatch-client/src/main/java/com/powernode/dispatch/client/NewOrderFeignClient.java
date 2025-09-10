package com.powernode.dispatch.client;

import com.powernode.common.result.Result;
import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import com.powernode.model.vo.order.NewOrderDataVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(value = "service-dispatch")
public interface NewOrderFeignClient {

    @PostMapping("/dispatch/newOrder/addAndStartTask")
    Result<Long> addAndStartTask(@RequestBody NewOrderTaskVo newOrderTaskVo);

    @GetMapping("/dispatch/newOrderfindNewOrderQueueData/{driverId}")
    Result<List<NewOrderDataVo>> findNewOrderQueueData(@PathVariable Long driverId);

    @DeleteMapping("/dispatch/newOrder/cleanNewOrderQueueData/{jobId}")
    Result<Boolean> cleanNewOrderQueueData(@PathVariable Long driverId);


}