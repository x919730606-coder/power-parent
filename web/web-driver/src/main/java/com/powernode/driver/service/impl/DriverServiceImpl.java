package com.powernode.driver.service.impl;


import com.powernode.common.constant.RedisConstant;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.dispatch.client.NewOrderFeignClient;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.driver.service.DriverService;
import com.powernode.map.client.LocationFeignClient;
import com.powernode.model.form.driver.DriverFaceModelForm;
import com.powernode.model.form.driver.UpdateDriverAuthInfoForm;
import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverServiceImpl implements DriverService {

    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private LocationFeignClient locationFeignClient;
    @Resource
    private NewOrderFeignClient newOrderFeignClient;

    @Override
    public String login(String code) {
        Long driverId = driverInfoFeignClient.login(code).getData();

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token, driverId.toString());
        return token;
    }

    @Override
    public DriverLoginVo getDriverLoginInfo(Long driverId) {
        return driverInfoFeignClient.getDriverLoginInfo(driverId).getData();
    }

    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        return driverInfoFeignClient.getDriverAuthInfo(driverId).getData();
    }

    @Override
    public Boolean updateDriverAuthStatus(UpdateDriverAuthInfoForm driverAuthInfoForm) {
        return driverInfoFeignClient.updateDriverAuthStatus(driverAuthInfoForm).getData();
    }

    @Override
    public Boolean createDriverFaceModel(DriverFaceModelForm driverFaceModelForm) {
        return driverInfoFeignClient.createDriverFaceModel(driverFaceModelForm).getData();
    }

    @Override
    public Boolean isFaceRecognition(Long driverId) {

        return driverInfoFeignClient.isFaceRecognition(driverId).getData();

    }

    @Override
    public Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm) {

        return driverInfoFeignClient.verifyDriverFace(driverFaceModelForm).getData();

    }

    @Override
    public Boolean startService(Long driverId) {

        DriverLoginVo driverLoginVo = driverInfoFeignClient.getDriverLoginInfo(driverId).getData();

        if (driverLoginVo.getAuthStatus() != 2){
            throw new PowerException(ResultCodeEnum.AUTH_ERROR);
        }

        Boolean isFace = driverInfoFeignClient.isFaceRecognition(driverId).getData();

        if(!isFace){
            throw new PowerException(ResultCodeEnum.FACE_ERROR);
        }

        driverInfoFeignClient.updateServiceStatus(driverId, 1);

        locationFeignClient.removeDriverLocation(driverId);

        newOrderFeignClient.cleanNewOrderQueueData(driverId);

        return true;

    }

    @Override
    public Boolean stopService(Long driverId) {

        driverInfoFeignClient.updateServiceStatus(driverId, 0);

        locationFeignClient.removeDriverLocation(driverId);

        newOrderFeignClient.cleanNewOrderQueueData(driverId);

        return true;

    }

}
