package com.powernode.customer.service;

import com.powernode.model.form.customer.ExpectOrderForm;
import com.powernode.model.form.customer.SubmitOrderForm;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.vo.customer.ExpectOrderVo;
import com.powernode.model.vo.driver.DriverInfoVo;
import com.powernode.model.vo.map.DrivingLineVo;
import com.powernode.model.vo.map.OrderLocationVo;
import com.powernode.model.vo.map.OrderServiceLastLocationVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.OrderInfoVo;

public interface OrderService {

    ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm);

    Long addOrder(SubmitOrderForm submitOrderForm);

    Integer queryOrderStatus(Long orderId);

    CurrentOrderInfoVo searchCustomerCurrentOrderInfo(Long customerId);

    OrderInfoVo getOrderInfo(Long orderId, Long customerId);

    DriverInfoVo getDriverInfo(Long orderId, Long customerId);

    OrderLocationVo getCacheOrderLocation(Long orderId);

    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm drivingLineForm);

    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);
}
