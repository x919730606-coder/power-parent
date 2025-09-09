package com.powernode.dispatch.controller;

import com.powernode.common.result.Result;
import com.powernode.dispatch.service.NewOrderService;
import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "配送员新订单接口管理")
@RestController
@RequestMapping("/dispatch/newOrder")
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderController {

    @Resource
    private NewOrderService newOrderService;

    @Operation(summary = "添加新订单并启动任务")
    @PostMapping("/addAndStartTask")
    public Result<Long> addAndStartTask(@RequestBody NewOrderTaskVo newOrderTaskVo) {

        Long jobId = newOrderService.addAndStartTask(newOrderTaskVo);
        return Result.ok(jobId);

    }



}

