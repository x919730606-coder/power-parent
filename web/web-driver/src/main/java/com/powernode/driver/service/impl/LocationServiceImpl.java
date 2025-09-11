package com.powernode.driver.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.driver.service.LocationService;
import com.powernode.map.client.LocationFeignClient;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.form.map.OrderServiceLocationForm;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.form.map.UpdateOrderLocationForm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private LocationService locationService;
    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;
    @Autowired
    private LocationFeignClient locationFeignClient;

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm driverLocationForm) {

        DriverSet driverSet = driverInfoFeignClient.getDriverSet(driverLocationForm.getDriverId()).getData();

        if (driverSet.getServiceStatus() == 1){
            return locationService.updateDriverLocation(driverLocationForm);
        }

        throw new PowerException(ResultCodeEnum.NO_START_SERVICE);

    }

    @Override
    public Boolean updateDriverLocationToCache(UpdateOrderLocationForm orderLocationForm) {

        return locationService.updateDriverLocationToCache(orderLocationForm);

    }

    @Override
    public Boolean updateOrderLocation(List<OrderServiceLocationForm> orderServiceLocations) {

        return locationFeignClient.saveOrderServiceLocation(orderServiceLocations).getData();

    }

}
