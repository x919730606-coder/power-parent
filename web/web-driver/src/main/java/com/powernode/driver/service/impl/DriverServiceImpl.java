package com.powernode.driver.service.impl;


import com.powernode.common.constant.RedisConstant;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.driver.service.DriverService;
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

}
