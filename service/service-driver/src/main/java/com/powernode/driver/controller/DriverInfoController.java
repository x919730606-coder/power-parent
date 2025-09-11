package com.powernode.driver.controller;

import com.powernode.common.result.Result;
import com.powernode.driver.service.CosService;
import com.powernode.driver.service.DriverInfoService;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.form.driver.DriverFaceModelForm;
import com.powernode.model.form.driver.UpdateDriverAuthInfoForm;
import com.powernode.model.vo.driver.CosUploadVo;
import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "配送员API接口管理")
@RestController
@RequestMapping(value="/driver/info")
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverInfoController {

    @Resource
    private DriverInfoService driverInfoService;


    @Operation(summary = "配送员登陆")
    @GetMapping("/login/{code}")
    public Result<Long> login(@PathVariable String code) {
        return Result.ok(driverInfoService.login(code));
    }

    @Operation(summary = "获取配送员信息")
    @GetMapping("/getDriverLoginInfo/{driverId}")
	public Result<DriverLoginVo> getDriverLoginInfo(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.getDriverLoginInfo(driverId));
    }

    @Operation(summary = "获取配送员认证信息")
    @GetMapping("/getDriverAuthInfo/{driverId}")
    public Result<DriverAuthInfoVo> getDriverAuthInfo(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.getDriverAuthInfo(driverId));
    }

    @Operation(summary = "更新配送员认证信息")
    @PostMapping("/updateDriverAuthStatus")
    public Result<Boolean> updateDriverAuthStatus(@RequestBody UpdateDriverAuthInfoForm driverAuthInfoForm) {
        return Result.ok(driverInfoService.updateDriverAuthStatus(driverAuthInfoForm));
    }

    @Operation(summary = "创建配送员人脸模型")
    @PostMapping("/createDriverFaceModel")
    public Result<Boolean> createDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm){
        return Result.ok(driverInfoService.createDriverFaceModel(driverFaceModelForm));
    }

    @Operation(summary = "查询配送员的个性化设置")
    @GetMapping("/getDriverSet/{driverId}")
    public Result<DriverSet> getDriverSet(@PathVariable Long driverId) {

        return Result.ok(driverInfoService.getDriverSet(driverId));

    }

    @Operation(summary = "查询配送员是否进行人脸识别")
    @GetMapping("/isFaceRecognition/{driverId}")
    public Result<Boolean> isFaceRecognition(@PathVariable Long driverId) {

        return Result.ok(driverInfoService.isFaceRecognition(driverId));

    }

    @Operation(summary = "配送员人脸识别")
    @PostMapping("/verifyDriverFace")
    public Result<Boolean> verifyDriverFace(@RequestBody DriverFaceModelForm driverFaceModelForm) {

        return Result.ok(driverInfoService.verifyDriverFace(driverFaceModelForm));

    }

    @Operation(summary = "修改配送员接单状态")
    @GetMapping("/updateServiceStatus/{driverId}/{status}")
    public Result<Boolean> updateServiceStatus(@PathVariable Long driverId, @PathVariable Integer status) {

        return Result.ok(driverInfoService.updateServiceStatus(driverId, status));

    }

    @Operation(summary = "配送员信息")
    @GetMapping("/getDriverInfoOrder/{driverId}")
    public Result<DriverInfoVo> getDriverInfoOrder(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.getDriverInfoOrder(driverId));
    }
}

