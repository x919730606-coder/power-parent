package com.powernode.map.service.impl;


import com.powernode.common.constant.RedisConstant;
import com.powernode.common.constant.SystemConstant;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.map.service.LocationService;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.form.map.SearchNearByDriverForm;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.vo.map.NearByDriverVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

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

    @Override
    public List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm){

        Point point = new Point(searchNearByDriverForm.getLongitude().doubleValue(), searchNearByDriverForm.getLatitude().doubleValue());

        Distance distance = new Distance(SystemConstant.NEARBY_DRIVER_RADIUS, RedisGeoCommands.DistanceUnit.KILOMETERS);

        Circle circle = new Circle(point, distance);

        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = RedisGeoCommands
                .GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<String>> result = redisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_LOCATION, circle, geoRadiusCommandArgs);

        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = result.getContent();

        List<NearByDriverVo> list = new ArrayList<>();

        if (CollectionUtils.isEmpty(content)){
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> iterator = content.iterator();

            while (iterator.hasNext()){
                GeoResult<RedisGeoCommands.GeoLocation<String>> item = iterator.next();
                long driverId = Long.parseLong(item.getContent().getName());
                BigDecimal currentDistance = new BigDecimal(item.getDistance().getValue()).setScale(2, RoundingMode.HALF_UP);
                DriverSet driverSet = driverInfoFeignClient.getDriverSet(driverId).getData();

                if (driverSet.getAcceptDistance().doubleValue() != 0 && driverSet.getAcceptDistance().subtract(currentDistance).doubleValue() < 0){
                    continue;
                }

                if (driverSet.getOrderDistance().doubleValue() != 0 && driverSet.getOrderDistance().subtract(searchNearByDriverForm.getMileageDistance()).doubleValue() < 0){
                    continue;
                }

                NearByDriverVo nearByDriverVo = new NearByDriverVo();
                nearByDriverVo.setDriverId(driverId);
                nearByDriverVo.setDistance(currentDistance);
                list.add(nearByDriverVo);
            }
        }

    return list;

    }

}
