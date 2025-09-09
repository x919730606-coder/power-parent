package com.powernode.dispatch.hendler;

import com.powernode.dispatch.service.NewOrderService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class JobHandler {

    @Resource
    private NewOrderService newOrderService;

    @XxlJob("newOrderTaskHandler")
    public void newOrderTaskHandler(){

        long jobId = XxlJobHelper.getJobId();

        newOrderService.executeTask(jobId);

    }
}
