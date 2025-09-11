package com.powernode.driver.service;

import com.powernode.model.form.map.OrderServiceLocationForm;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.form.map.UpdateOrderLocationForm;

import java.util.List;

public interface LocationService {


    Boolean updateDriverLocation(UpdateDriverLocationForm driverLocationForm);

    Boolean updateDriverLocationToCache(UpdateOrderLocationForm orderLocationForm);

    Boolean updateOrderLocation(List<OrderServiceLocationForm> orderServiceLocations);
}
