package com.powernode.driver.service;

import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.NewOrderDataVo;

import java.util.List;

public interface OrderService {


    Integer queryOrderStatus(Long orderId);

    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);
}
