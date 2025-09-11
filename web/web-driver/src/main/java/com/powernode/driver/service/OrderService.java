package com.powernode.driver.service;

import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.form.order.StartDriveForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
import com.powernode.model.vo.map.DrivingLineVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.NewOrderDataVo;
import com.powernode.model.vo.order.OrderInfoVo;

import java.util.List;

public interface OrderService {


    Integer queryOrderStatus(Long orderId);

    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    OrderInfoVo getOrderInfo(Long orderId, Long driverId);

    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm drivingLineForm);

    Boolean driverArrivedStartLocation(Long orderId, Long driverId);

    Boolean updateOrderCart(UpdateOrderCartForm orderCartForm);

    Boolean startDrive(StartDriveForm startDriveForm);
}
