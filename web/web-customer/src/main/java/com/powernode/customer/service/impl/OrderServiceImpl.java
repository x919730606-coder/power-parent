package com.powernode.customer.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.customer.service.OrderService;
import com.powernode.dispatch.client.NewOrderFeignClient;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.map.client.MapFeignClient;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.customer.ExpectOrderForm;
import com.powernode.model.form.customer.SubmitOrderForm;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.form.rules.FeeRuleRequestForm;
import com.powernode.model.vo.customer.ExpectOrderVo;
import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import com.powernode.model.vo.driver.DriverInfoVo;
import com.powernode.model.vo.map.DrivingLineVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.OrderInfoVo;
import com.powernode.model.vo.rules.FeeRuleResponseVo;
import com.powernode.order.client.OrderInfoFeignClient;
import com.powernode.rules.client.FeeRuleFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Resource
    private MapFeignClient mapFeignClient;
    @Resource
    private FeeRuleFeignClient feeRuleFeignClient;
    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;
    @Autowired
    private NewOrderFeignClient newOrderFeignClient;
    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Override
    public ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm) {

        CalculateDrivingLineForm lineForm = new CalculateDrivingLineForm();
        BeanUtils.copyProperties(expectOrderForm, lineForm);

        DrivingLineVo drivingLineVo = mapFeignClient.calculateDrivingLine(lineForm).getData();

        FeeRuleRequestForm feeRuleRequestForm = new FeeRuleRequestForm();
        feeRuleRequestForm.setDistance(drivingLineVo.getDistance());
        feeRuleRequestForm.setStartTime(new Date());
        feeRuleRequestForm.setWaitMinute(0);

        FeeRuleResponseVo data = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();

        ExpectOrderVo expectOrderVo = new ExpectOrderVo();
        expectOrderVo.setDrivingLineVo(drivingLineVo);
        expectOrderVo.setFeeRuleResponseVo(data);

        return expectOrderVo;

    }

    @Override
    public Long addOrder(SubmitOrderForm submitOrderForm){

        CalculateDrivingLineForm calculateDrivingLineForm = new CalculateDrivingLineForm();
        BeanUtils.copyProperties(submitOrderForm, calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();

        FeeRuleRequestForm feeRuleRequestForm = new FeeRuleRequestForm();
        feeRuleRequestForm.setDistance(drivingLineVo.getDistance());
        feeRuleRequestForm.setStartTime(new Date());
        feeRuleRequestForm.setWaitMinute(0);

        FeeRuleResponseVo data = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();

        OrderInfoForm orderInfoForm = new OrderInfoForm();
        BeanUtils.copyProperties(submitOrderForm, orderInfoForm);
        orderInfoForm.setExpectDistance(drivingLineVo.getDistance());
        orderInfoForm.setExpectAmount(data.getTotalAmount());

        Long orderId = orderInfoFeignClient.addOrderInfo(orderInfoForm).getData();

        NewOrderTaskVo orderTaskVo = new NewOrderTaskVo();
        BeanUtils.copyProperties(orderInfoForm, orderTaskVo);
        orderTaskVo.setOrderId(orderId);
        orderTaskVo.setExpectTime(drivingLineVo.getDuration());
        orderTaskVo.setCreateTime(new Date());

        newOrderFeignClient.addAndStartTask(orderTaskVo).getData();

        return orderId;

    }

    @Override
    public Integer queryOrderStatus(Long orderId) {

        return orderInfoFeignClient.queryOrderStatus(orderId).getData();

    }

    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrderInfo(Long customerId) {

        return orderInfoFeignClient.searchCustomerCurrentOrderInfo(customerId).getData();

    }

    @Override
    public OrderInfoVo getOrderInfo(Long orderId, Long customerId) {

        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();
        if (orderInfo.getCustomerId().longValue() != customerId.longValue()){
            throw new PowerException(ResultCodeEnum.ILLEGAL_REQUEST);
        }
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        BeanUtils.copyProperties(orderInfo, orderInfoVo);
        orderInfoVo.setOrderId(orderId);
        return orderInfoVo;

    }

    @Override
    public DriverInfoVo getDriverInfo(Long orderId, Long customerId) {

        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();
        if (orderInfo.getCustomerId().longValue() != customerId.longValue()){
            throw new PowerException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        return driverInfoFeignClient.getDriverInfoOrder(orderInfo.getDriverId()).getData();

    }

}
