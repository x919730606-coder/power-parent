package com.powernode.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.common.constant.RedisConstant;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.model.entity.order.OrderBill;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.entity.order.OrderMonitor;
import com.powernode.model.entity.order.OrderProfitsharing;
import com.powernode.model.enums.OrderStatus;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.form.order.StartDriveForm;
import com.powernode.model.form.order.UpdateOrderBillForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.order.mapper.OrderBillMapper;
import com.powernode.order.mapper.OrderInfoMapper;
import com.powernode.order.mapper.OrderProfitsharingMapper;
import com.powernode.order.service.OrderInfoService;
import com.powernode.order.service.OrderMonitorService;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Resource
    private OrderMonitorService orderMonitorService;
    @Autowired
    private OrderBillMapper orderBillMapper;
    @Resource
    private OrderProfitsharingMapper orderProfitsharingMapper;

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

    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrderInfo(Long customerId) {

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getCustomerId, customerId);

        Integer[] statusArray = {
                OrderStatus.ACCEPTED.getStatus(),
                OrderStatus.DRIVER_ARRIVED.getStatus(),
                OrderStatus.UPDATE_CART_INFO.getStatus(),
                OrderStatus.START_SERVICE.getStatus(),
                OrderStatus.END_SERVICE.getStatus(),
                OrderStatus.UNPAID.getStatus()
        };

        queryWrapper.in(OrderInfo::getStatus, statusArray);
        queryWrapper.orderByDesc(OrderInfo::getId);
        queryWrapper.last("limit 1");

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();

        if (orderInfo != null){

            currentOrderInfoVo.setOrderId(orderInfo.getId());
            currentOrderInfoVo.setIsHasCurrentOrder(true);
            currentOrderInfoVo.setStatus(orderInfo.getStatus());

        }else {
            currentOrderInfoVo.setIsHasCurrentOrder(false);
        }

        return currentOrderInfoVo;

    }

    @Override
    public CurrentOrderInfoVo searchDriverCurrentOrderInfo(Long driverId) {

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getCustomerId, driverId);

        Integer[] statusArray = {
                OrderStatus.ACCEPTED.getStatus(),
                OrderStatus.DRIVER_ARRIVED.getStatus(),
                OrderStatus.UPDATE_CART_INFO.getStatus(),
                OrderStatus.START_SERVICE.getStatus(),
                OrderStatus.END_SERVICE.getStatus(),
                OrderStatus.UNPAID.getStatus()
        };

        queryWrapper.in(OrderInfo::getStatus, statusArray);
        queryWrapper.orderByDesc(OrderInfo::getId);
        queryWrapper.last("limit 1");

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();

        if (orderInfo != null){

            currentOrderInfoVo.setOrderId(orderInfo.getId());
            currentOrderInfoVo.setIsHasCurrentOrder(true);
            currentOrderInfoVo.setStatus(orderInfo.getStatus());

        }else {
            currentOrderInfoVo.setIsHasCurrentOrder(false);
        }

        return currentOrderInfoVo;

    }

    @Override
    public  Boolean driverArrivedStartLocation(Long orderId, Long driverId){

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, orderId);
        queryWrapper.eq(OrderInfo::getDriverId, driverId);

        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.DRIVER_ARRIVED.getStatus());
        updateOrderInfo.setArriveTime(new Date());

        orderInfoMapper.update(updateOrderInfo, queryWrapper);

        return true;

    }

    @Override
    public Boolean updateOrderCart(UpdateOrderCartForm orderCartForm){

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, orderCartForm.getOrderId());
        queryWrapper.eq(OrderInfo::getDriverId, orderCartForm.getDriverId());

        OrderInfo updateOrderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderCartForm, updateOrderInfo);
        updateOrderInfo.setStatus(OrderStatus.UPDATE_CART_INFO.getStatus());

        return true;

    }

    @Override
    public Boolean startDrive(StartDriveForm startDriveForm){

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, startDriveForm.getOrderId());
        queryWrapper.eq(OrderInfo::getDriverId, startDriveForm.getDriverId());

        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.START_SERVICE.getStatus());
        updateOrderInfo.setStartServiceTime(new Date());

        orderInfoMapper.update(updateOrderInfo, queryWrapper);

        OrderMonitor orderMonitor = new OrderMonitor();
        orderMonitor.setOrderId(startDriveForm.getOrderId());

        orderMonitorService.addOrderMonitor(orderMonitor);

        return true;

    }

    @Override
    public Long getOrderNumByTime(String startTIme, String endTime){

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(OrderInfo::getStartServiceTime, startTIme);
        queryWrapper.le(OrderInfo::getStartServiceTime, endTime);

        return orderInfoMapper.selectCount(queryWrapper);

    }

    @Transactional
    @Override
    public Boolean endDriver(UpdateOrderBillForm billForm){

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, billForm.getOrderId());
        queryWrapper.eq(OrderInfo::getDriverId, billForm.getDriverId());

        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.END_SERVICE.getStatus());
        updateOrderInfo.setRealAmount(billForm.getTotalAmount());
        updateOrderInfo.setFavourFee(billForm.getFavourFee());
        updateOrderInfo.setEndServiceTime(new Date());
        updateOrderInfo.setRealDistance(billForm.getRealDistance());

        orderInfoMapper.update(updateOrderInfo, queryWrapper);

        OrderBill orderBill = new OrderBill();
        BeanUtils.copyProperties(billForm, orderBill);
        orderBill.setOrderId(billForm.getOrderId());
        orderBill.setPayAmount(billForm.getTotalAmount());

        orderBillMapper.insert(orderBill);

        OrderProfitsharing orderProfitsharing = new OrderProfitsharing();
        BeanUtils.copyProperties(billForm, orderProfitsharing);
        orderProfitsharing.setOrderId(billForm.getOrderId());
        orderProfitsharing.setStatus(1);
        orderProfitsharingMapper.insert(orderProfitsharing);

        return true;

    }



}
