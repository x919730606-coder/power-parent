package com.powernode.dispatch.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.dispatch.client.XxlJobClient;
import com.powernode.dispatch.mapper.OrderJobMapper;
import com.powernode.dispatch.service.NewOrderService;
import com.powernode.model.entity.dispatch.OrderJob;
import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderServiceImpl implements NewOrderService {

    @Resource
    private OrderJobMapper orderJobMapper;
    @Resource
    private XxlJobClient xxlJobClient;


    @Override
    public Long addAndStartTask(NewOrderTaskVo newOrderTaskVo){

        LambdaQueryWrapper<OrderJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderJob::getOrderId, newOrderTaskVo.getOrderId());
        OrderJob orderJob = orderJobMapper.selectOne(queryWrapper);

        if (orderJob == null){

            Long jobId = xxlJobClient.addAndStart("newOrderTaskHandler", "", "0 0/1 * * * ?", "用户下单" + newOrderTaskVo.getOrderId());

            orderJob = new OrderJob();
            orderJob.setOrderId(newOrderTaskVo.getOrderId());
            orderJob.setJobId(jobId);
            orderJob.setParameter(JSONObject.toJSONString(newOrderTaskVo));

            orderJobMapper.insert(orderJob);

        }

        return orderJob.getJobId();

    }
}
