package com.powernode.map.service;

import com.powernode.model.form.map.OrderServiceLocationForm;
import com.powernode.model.form.map.SearchNearByDriverForm;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.form.map.UpdateOrderLocationForm;
import com.powernode.model.vo.map.NearByDriverVo;
import com.powernode.model.vo.map.OrderLocationVo;
import com.powernode.model.vo.map.OrderServiceLastLocationVo;

import java.math.BigDecimal;
import java.util.List;

public interface LocationService {

    Boolean updateDriverLocation(UpdateDriverLocationForm driverLocationForm);

    Boolean removeDriverLocation(Long driverId);

    List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm);

    Boolean updateDriverLocationToCache(UpdateOrderLocationForm orderLocationForm);

    OrderLocationVo getCacheOrderLocation(Long orderId);

    Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderServiceLocationFormList);

    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);

    BigDecimal calculateOrderRealDistance(Long orderId);
}
