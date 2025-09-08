package com.powernode.map.client;

import com.powernode.common.result.Result;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-map")
public interface LocationFeignClient {

    @PostMapping("/map/location/updateDriverLocation")
    Result<Boolean> updateDriverLocation(@RequestBody UpdateDriverLocationForm driverLocationForm);

    @PostMapping("/map/location/removeDriverLocation")
    Result<Boolean> removeDriverLocation(Long driverId);

}