package com.powernode.map.controller;

import com.powernode.common.result.Result;
import com.powernode.map.service.LocationService;
import com.powernode.model.form.map.OrderServiceLocationForm;
import com.powernode.model.form.map.SearchNearByDriverForm;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.form.map.UpdateOrderLocationForm;
import com.powernode.model.vo.map.NearByDriverVo;
import com.powernode.model.vo.map.OrderLocationVo;
import com.powernode.model.vo.map.OrderServiceLastLocationVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Tag(name = "位置API接口管理")
@RestController
@RequestMapping("/map/location")
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationController {

    @Resource
    private LocationService locationService;

    @Operation(summary = "更新配送员位置")
    @PostMapping("/updateDriverLocation")
    public Result<Boolean> updateDriverLocation(@RequestBody UpdateDriverLocationForm driverLocationForm) {

        return locationService.updateDriverLocation(driverLocationForm) ? Result.ok() : Result.fail();

    }

    @Operation(summary = "删除配送员位置")
    @DeleteMapping("/removeDriverLocation/{driverId}")
    public Result<Boolean> removeDriverLocation(@PathVariable Long driverId) {

        return Result.ok(locationService.removeDriverLocation(driverId));

    }

    @Operation(summary = "查询附近配送员")
    @PostMapping("/searchNearByDriver")
    public Result<List<NearByDriverVo>> searchNearByDriver(@RequestBody SearchNearByDriverForm searchNearByDriverForm) {

        return Result.ok(locationService.searchNearByDriver(searchNearByDriverForm));

    }

    @Operation(summary = "更新配送员位置到缓存")
    @PostMapping("/updateOrderLocationToCache")
    public Result<Boolean> updateOrderLocationToCache(@RequestBody UpdateOrderLocationForm orderLocationForm) {

        return locationService.updateDriverLocationToCache(orderLocationForm) ? Result.ok() : Result.fail();

    }

    @Operation(summary = "获取缓存中配送员的位置信息")
    @GetMapping("/getCacheOrderLocation/{orderId}")
    public Result<OrderLocationVo> getCacheOrderLocation(@PathVariable Long orderId) {

        return Result.ok(locationService.getCacheOrderLocation(orderId));

    }

    @Operation(summary = "批量保存订单服务位置信息")
    @PostMapping("/saveOrderServiceLocation")
    public Result<Boolean> saveOrderServiceLocation(@RequestBody List<OrderServiceLocationForm> orderServiceLocationFormList) {

        return Result.ok(locationService.saveOrderServiceLocation(orderServiceLocationFormList));

    }

    @Operation(summary = "获取订单服务最后位置信息")
    @GetMapping("/getOrderServiceLastLocation/{orderId}")
    public Result<OrderServiceLastLocationVo> getOrderServiceLastLocation(@PathVariable Long orderId) {

        return Result.ok(locationService.getOrderServiceLastLocation(orderId));

    }

    @Operation(summary = "计算订单真实距离")
    @GetMapping("/calculateOrderRealDistance/{orderId}")
    public Result<BigDecimal> calculateOrderRealDistance(@PathVariable Long orderId) {

        return Result.ok(locationService.calculateOrderRealDistance(orderId));

    }

}

