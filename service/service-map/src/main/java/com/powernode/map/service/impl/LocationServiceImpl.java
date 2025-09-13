package com.powernode.map.service.impl;


import com.powernode.common.constant.RedisConstant;
import com.powernode.common.constant.SystemConstant;
import com.powernode.common.util.LocationUtil;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.map.repository.OrderServiceLocationRepository;
import com.powernode.map.service.LocationService;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.entity.map.OrderServiceLocation;
import com.powernode.model.form.map.OrderServiceLocationForm;
import com.powernode.model.form.map.SearchNearByDriverForm;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.form.map.UpdateOrderLocationForm;
import com.powernode.model.vo.map.NearByDriverVo;
import com.powernode.model.vo.map.OrderLocationVo;
import com.powernode.model.vo.map.OrderServiceLastLocationVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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
    @Resource
    private OrderServiceLocationRepository orderServiceLocationRepository;
    @Resource
    private MongoTemplate mongoTemplate;

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

    @Override
    public Boolean updateDriverLocationToCache(UpdateOrderLocationForm orderLocationForm){

        OrderLocationVo orderLocationVo = new OrderLocationVo();
        orderLocationVo.setLatitude(orderLocationForm.getLatitude());
        orderLocationVo.setLongitude(orderLocationForm.getLongitude());

        redisTemplate.opsForValue().set(RedisConstant.UPDATE_ORDER_LOCATION + orderLocationForm.getOrderId(), orderLocationVo);

        return true;

    }

    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId){

        OrderLocationVo orderLocationVo = (OrderLocationVo) redisTemplate.opsForValue().get(RedisConstant.UPDATE_ORDER_LOCATION + orderId);
        return orderLocationVo;

    }

    @Override
    public Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderServiceLocationFormList){

        ArrayList<OrderServiceLocation> serviceLocations = new ArrayList<>();

        orderServiceLocationFormList.forEach(item -> {
            OrderServiceLocation serviceLocation = new OrderServiceLocation();
            BeanUtils.copyProperties(item, serviceLocation);
            serviceLocation.setId(ObjectId.get().toString());
            serviceLocation.setCreateTime(new Date());
            serviceLocations.add(serviceLocation);
        });

        orderServiceLocationRepository.saveAll(serviceLocations);

        return true;

    }

    @Override
    public OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId){

        Query query = new Query();
        query.addCriteria(Criteria.where("orderId").is(orderId));
        query.with(Sort.by(Sort.Direction.DESC, "createTime"));
        query.limit(1);

        OrderServiceLocation orderServiceLocation = mongoTemplate.findOne(query, OrderServiceLocation.class);

        OrderServiceLastLocationVo orderServiceLastLocationVo = new OrderServiceLastLocationVo();

        BeanUtils.copyProperties(orderServiceLocation, orderServiceLastLocationVo);

        return orderServiceLastLocationVo;

    }

    @Override
    public BigDecimal calculateOrderRealDistance(Long orderId){

        List<OrderServiceLocation> serviceLocationList = orderServiceLocationRepository.findByOrderIdOrderByCreateTimeAsc(orderId);

        double realDistance = 0;
        if (!CollectionUtils.isEmpty(serviceLocationList)){
            for (int i = 0,size = serviceLocationList.size() - 1; i < size ; i++){
                OrderServiceLocation start = serviceLocationList.get(i);
                OrderServiceLocation end = serviceLocationList.get(i + 1);

                double distance = LocationUtil.getDistance(start.getLatitude().doubleValue(),
                        start.getLongitude().doubleValue(),
                        end.getLatitude().doubleValue(),
                        end.getLongitude().doubleValue());

                realDistance += distance;
            }
        }

        if (realDistance == 0){
            return new BigDecimal(5);
        }

        return new BigDecimal(realDistance);

    }

}
