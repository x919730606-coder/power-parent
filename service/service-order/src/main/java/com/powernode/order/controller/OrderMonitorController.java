package com.powernode.order.controller;

import com.powernode.common.result.Result;
import com.powernode.model.entity.order.OrderMonitorRecord;
import com.powernode.order.service.OrderMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order/monitor")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderMonitorController {

    @Resource
    private OrderMonitorService orderMonitorService;

    @Operation(summary = "保存音频转换文本的信息到mongodb中")
    @PostMapping("saveOrderMonitorRecord")
    public Result<Boolean> saveOrderMonitorRecord(OrderMonitorRecord orderMonitorRecord) {

        return Result.ok(orderMonitorService.saveOrderMonitorRecord(orderMonitorRecord));

    }


}

