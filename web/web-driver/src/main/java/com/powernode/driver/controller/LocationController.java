package com.powernode.driver.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.driver.service.LocationService;
import com.powernode.model.form.map.OrderServiceLocationForm;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.form.map.UpdateOrderLocationForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "位置API接口管理")
@RestController
@RequestMapping(value="/location")
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationController {

    @Resource
    private LocationService locationService;

    @Operation(summary = "更新配送员位置")
    @PostMapping("/updateDriverLocation")
    @PowerLogin
    public Result<Boolean> updateDriverLocation(@RequestBody UpdateDriverLocationForm driverLocationForm){

        Long userId = AuthContextHolder.getUserId();
        driverLocationForm.setDriverId(userId);

        Boolean b = locationService.updateDriverLocation(driverLocationForm);
        return Result.ok(b);

    }

    @Operation(summary = "更新配送员位置到缓存")
    @PostMapping("/updateOrderLocationToCache")
    public Result<Boolean> updateOrderLocationToCache(@RequestBody UpdateOrderLocationForm orderLocationForm){

        return Result.ok(locationService.updateDriverLocationToCache(orderLocationForm));

    }

    @Operation(summary = "配送员配送中上传位置信息")
    @PostMapping("/saveOrderServiceLocation")
    public Result<Boolean> updateOrderLocation(@RequestBody List<OrderServiceLocationForm> orderServiceLocationFormList){

        return Result.ok(locationService.updateOrderLocation(orderServiceLocationFormList));

    }
	

}

