package com.powernode.customer.service;

import com.powernode.model.form.customer.ExpectOrderForm;
import com.powernode.model.form.customer.SubmitOrderForm;
import com.powernode.model.vo.customer.ExpectOrderVo;

public interface OrderService {

    ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm);

    Long addOrder(SubmitOrderForm submitOrderForm);

    Integer queryOrderStatus(Long orderId);
}
