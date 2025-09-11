package com.powernode.driver.service;

import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.form.map.UpdateOrderLocationForm;

public interface LocationService {


    Boolean updateDriverLocation(UpdateDriverLocationForm driverLocationForm);

    Boolean updateDriverLocationToCache(UpdateOrderLocationForm orderLocationForm);
}
