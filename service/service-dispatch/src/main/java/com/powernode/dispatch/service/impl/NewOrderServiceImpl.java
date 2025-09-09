package com.powernode.dispatch.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.common.constant.RedisConstant;
import com.powernode.common.result.Result;
import com.powernode.dispatch.client.XxlJobClient;
import com.powernode.dispatch.mapper.OrderJobMapper;
import com.powernode.dispatch.service.NewOrderService;
import com.powernode.map.client.LocationFeignClient;
import com.powernode.model.entity.dispatch.OrderJob;
import com.powernode.model.enums.OrderStatus;
import com.powernode.model.form.map.SearchNearByDriverForm;
import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import com.powernode.model.vo.map.NearByDriverVo;
import com.powernode.model.vo.order.NewOrderDataVo;
import com.powernode.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderServiceImpl implements NewOrderService {

    @Resource
    private OrderJobMapper orderJobMapper;
    @Resource
    private XxlJobClient xxlJobClient;
    @Resource
    private LocationFeignClient locationFeignClient;
    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;
    @Resource
    private RedisTemplate redisTemplate;


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

    @Override
    public Boolean executeTask(Long jobId){

        LambdaQueryWrapper<OrderJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderJob::getJobId, jobId);
        OrderJob orderJob = orderJobMapper.selectOne(queryWrapper);
        NewOrderTaskVo newOrderTaskVo = JSONObject.parseObject(orderJob.getParameter(), NewOrderTaskVo.class);

        Integer orderStatus = orderInfoFeignClient.queryOrderStatus(newOrderTaskVo.getOrderId()).getData();

        if (orderStatus.intValue() != OrderStatus.WAITING_ACCEPT.getStatus().intValue()){
            xxlJobClient.stopJob(jobId);
            return true;
        }

        SearchNearByDriverForm searchNearByDriverForm = new SearchNearByDriverForm();
        searchNearByDriverForm.setLongitude(newOrderTaskVo.getStartPointLongitude());
        searchNearByDriverForm.setLatitude(newOrderTaskVo.getStartPointLatitude());
        searchNearByDriverForm.setMileageDistance(newOrderTaskVo.getExpectDistance());

        List<NearByDriverVo> nearByDriverVoList = locationFeignClient.searchNearByDriver(searchNearByDriverForm).getData();

        nearByDriverVoList.forEach(driver -> {

            String repeatKey = RedisConstant.DRIVER_ORDER_REPEAT_LIST + newOrderTaskVo.getOrderId();
            Boolean isMember = redisTemplate.opsForSet().isMember(repeatKey, driver.getDriverId());

            if (!isMember){

                redisTemplate.opsForSet().add(repeatKey, driver.getDriverId());
                redisTemplate.expire(repeatKey,RedisConstant.DRIVER_ORDER_REPEAT_LIST_EXPIRES_TIME, TimeUnit.MINUTES);

                NewOrderDataVo newOrderDataVo = new NewOrderDataVo();
                BeanUtils.copyProperties(newOrderTaskVo, newOrderDataVo);

                String key = RedisConstant.DRIVER_ORDER_TEMP_LIST + driver.getDriverId();

                redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(newOrderDataVo));

            }
        });

        return true;

    }
}
