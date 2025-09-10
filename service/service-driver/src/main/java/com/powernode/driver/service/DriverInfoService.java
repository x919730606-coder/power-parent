package com.powernode.driver.service;

import com.powernode.model.entity.driver.DriverInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.form.driver.DriverFaceModelForm;
import com.powernode.model.form.driver.UpdateDriverAuthInfoForm;
import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;

public interface DriverInfoService extends IService<DriverInfo> {

    Long login(String code);

    DriverLoginVo getDriverLoginInfo(Long driverId);

    DriverAuthInfoVo getDriverAuthInfo(Long driverId);

    Boolean updateDriverAuthStatus(UpdateDriverAuthInfoForm driverAuthInfoForm);

    Boolean createDriverFaceModel(DriverFaceModelForm driverFaceModelForm);

    DriverSet getDriverSet(Long driverId);

    Boolean isFaceRecognition(Long driverId);
}
