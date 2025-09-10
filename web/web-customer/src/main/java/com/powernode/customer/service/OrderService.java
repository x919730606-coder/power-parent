package com.powernode.customer.service;

import com.powernode.model.form.customer.ExpectOrderForm;
import com.powernode.model.form.customer.SubmitOrderForm;
import com.powernode.model.vo.customer.ExpectOrderVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;

public interface OrderService {

    ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm);

    Long addOrder(SubmitOrderForm submitOrderForm);

    Integer queryOrderStatus(Long orderId);

    CurrentOrderInfoVo searchCustomerCurrentOrderInfo(Long customerId);
}
