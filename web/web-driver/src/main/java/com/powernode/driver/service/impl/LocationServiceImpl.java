package com.powernode.driver.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.driver.service.LocationService;
import com.powernode.map.client.LocationFeignClient;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private LocationFeignClient locationFeignClient;
    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm driverLocationForm) {

        DriverSet driverSet = driverInfoFeignClient.getDriverSet(driverLocationForm.getDriverId()).getData();

        if (driverSet.getServiceStatus() == 1){
            return locationFeignClient.updateDriverLocation(driverLocationForm).getData();
        }

        throw new PowerException(ResultCodeEnum.NO_START_SERVICE);

    }

}
