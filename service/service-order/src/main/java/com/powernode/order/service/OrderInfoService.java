package com.powernode.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.form.order.StartDriveForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
import com.powernode.model.vo.order.CurrentOrderInfoVo;

public interface OrderInfoService extends IService<OrderInfo> {

    Long addOrderInfo(OrderInfoForm orderInfoForm);

    Integer queryOrderStatus(Long orderId);

    Boolean robNewOrder(Long driverId, Long orderId);

    CurrentOrderInfoVo searchCustomerCurrentOrderInfo(Long customerId);

    CurrentOrderInfoVo searchDriverCurrentOrderInfo(Long driverId);

    Boolean driverArrivedStartLocation(Long orderId, Long driverId);

    Boolean updateOrderCart(UpdateOrderCartForm orderCartForm);

    Boolean startDrive(StartDriveForm startDriveForm);
}
