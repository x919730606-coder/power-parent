package com.powernode.driver.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.dispatch.client.NewOrderFeignClient;
import com.powernode.driver.service.OrderService;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.NewOrderDataVo;
import com.powernode.model.vo.order.OrderInfoVo;
import com.powernode.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;
    @Resource
    private NewOrderFeignClient newOrderFeignClient;

    @Override
    public Integer queryOrderStatus(Long orderId) {

        return orderInfoFeignClient.queryOrderStatus(orderId).getData();

    }

    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {

        return newOrderFeignClient.findNewOrderQueueData(driverId).getData();

    }

    @Override
    public CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId) {

        return orderInfoFeignClient.searchDriverCurrentOrderInfo(driverId).getData();

    }

    @Override
    public OrderInfoVo getOrderInfo(Long orderId, Long driverId) {

        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();
        if (orderInfo.getDriverId().intValue() != driverId.intValue()){
            throw new PowerException(ResultCodeEnum.ILLEGAL_REQUEST);
        }
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        BeanUtils.copyProperties(orderInfo, orderInfoVo);
        orderInfoVo.setOrderId(orderId);
        return orderInfoVo;

    }
}
