package com.powernode.map.client;

import com.powernode.common.result.Result;
import com.powernode.model.form.map.SearchNearByDriverForm;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.vo.map.NearByDriverVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "service-map")
public interface LocationFeignClient {

    @PostMapping("/map/location/updateDriverLocation")
    Result<Boolean> updateDriverLocation(@RequestBody UpdateDriverLocationForm driverLocationForm);

    @PostMapping("/map/location/removeDriverLocation")
    Result<Boolean> removeDriverLocation(Long driverId);

    @PostMapping("/map/location/searchNearByDriver")
    Result<List<NearByDriverVo>> searchNearByDriver(@RequestBody SearchNearByDriverForm searchNearByDriverForm);

}