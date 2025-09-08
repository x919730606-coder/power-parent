package com.powernode.map.service;

import com.powernode.model.form.map.UpdateDriverLocationForm;

public interface LocationService {

    Boolean updateDriverLocation(UpdateDriverLocationForm driverLocationForm);

    Boolean removeDriverLocation(Long driverId);
}
