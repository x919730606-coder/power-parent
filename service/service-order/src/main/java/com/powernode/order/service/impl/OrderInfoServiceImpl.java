package com.powernode.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.common.config.redisson.RedissonConfig;
import com.powernode.common.constant.RedisConstant;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.enums.OrderStatus;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.order.mapper.OrderInfoMapper;
import com.powernode.order.service.OrderInfoService;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public Long addOrderInfo(OrderInfoForm orderInfoForm){

        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderInfoForm, orderInfo);
        String orderNo = UUID.randomUUID().toString().replaceAll("-", "");

        orderInfo.setStatus(OrderStatus.WAITING_ACCEPT.getStatus());

        orderInfoMapper.insert(orderInfo);

        redisTemplate.opsForValue().set(RedisConstant.ORDER_ACCEPT_MARK + orderInfo.getId(),1);

        return orderInfo.getId();

    }

    @Override
    public Integer queryOrderStatus(Long orderId) {

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(OrderInfo::getStatus);
        queryWrapper.eq(OrderInfo::getId, orderId);

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        return orderInfo.getStatus();

    }

    @Override
    public Boolean robNewOrder(Long driverId, Long orderId){

        if (!redisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK + orderId)){
            throw new PowerException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
        }

        RLock lock = redissonClient.getLock(RedisConstant.ROB_NEW_ORDER_LOCK + orderId);

        try {
            boolean flag = lock.tryLock(RedisConstant.ROB_NEW_ORDER_LOCK_WAIT_TIME, RedisConstant.ROB_NEW_ORDER_LOCK_LEASE_TIME, TimeUnit.SECONDS);

            if (flag){

                if (!redisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK + orderId)){
                    throw new PowerException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
                }

                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setId(orderId);
                orderInfo.setStatus(OrderStatus.ACCEPTED.getStatus());
                orderInfo.setAcceptTime(new Date());
                orderInfo.setDriverId(driverId);
                orderInfoMapper.updateById(orderInfo);

                redisTemplate.delete(RedisConstant.ORDER_ACCEPT_MARK + orderId);

            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }

        return true;

    }

}
