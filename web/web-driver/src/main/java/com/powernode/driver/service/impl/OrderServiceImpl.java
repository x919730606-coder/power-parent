package com.powernode.driver.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.dispatch.client.NewOrderFeignClient;
import com.powernode.driver.service.OrderService;
import com.powernode.map.client.MapFeignClient;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.form.order.StartDriveForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
import com.powernode.model.vo.map.DrivingLineVo;
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
    @Resource
    private MapFeignClient mapFeignClient;

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

    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm drivingLineForm) {

        return mapFeignClient.calculateDrivingLine(drivingLineForm).getData();

    }

    @Override
    public Boolean driverArrivedStartLocation(Long orderId, Long driverId){

        return orderInfoFeignClient.driverArrivedStartLocation(orderId,driverId).getData();

    }

    @Override
    public Boolean updateOrderCart(UpdateOrderCartForm orderCartForm){

        return orderInfoFeignClient.updateOrderCart(orderCartForm).getData();

    }

    @Override
    public Boolean startDrive(StartDriveForm startDriveForm){

        return orderInfoFeignClient.startDrive(startDriveForm).getData();

    }
}
