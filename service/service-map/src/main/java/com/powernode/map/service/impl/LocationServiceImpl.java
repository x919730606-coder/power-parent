package com.powernode.map.service.impl;


import com.powernode.common.constant.RedisConstant;
import com.powernode.map.service.LocationService;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm driverLocationForm){

        Point point = new Point(driverLocationForm.getLongitude().doubleValue(),
                driverLocationForm.getLatitude().doubleValue());

        redisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION, point, driverLocationForm.getDriverId().toString());

        return true;

    }

    @Override
    public Boolean removeDriverLocation(Long driverId){

        redisTemplate.opsForGeo().remove(RedisConstant.DRIVER_GEO_LOCATION, driverId.toString());

        return true;

    }
}
