package com.powernode.driver.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.driver.service.DriverService;
import com.powernode.model.form.driver.DriverFaceModelForm;
import com.powernode.model.form.driver.UpdateDriverAuthInfoForm;
import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "配送员API接口管理")
@RestController
@RequestMapping(value="/driver")
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverController {

    @Resource
    private DriverService driverService;

    @Operation(summary = "配送员登陆")
    @GetMapping("/login/{code}")
    public Result<String> login(@PathVariable String code) {

        return Result.ok(driverService.login(code));

    }

    @Operation(summary = "获取配送员信息")
    @GetMapping("/getDriverLoginInfo/")
    @PowerLogin
    public Result<DriverLoginVo> getDriverLoginInfo() {

        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(driverService.getDriverLoginInfo(driverId));

    }

    @Operation(summary = "获取配送员认证信息")
    @GetMapping("/getDriverAuthInfo/")
    @PowerLogin
    public Result<DriverAuthInfoVo> getDriverAuthInfo() {

        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(driverService.getDriverAuthInfo(driverId));

    }

    @Operation(summary = "更新配送员认证信息")
    @PostMapping("/updateDriverAuthStatus")
    @PowerLogin
    public Result<Boolean> updateDriverAuthStatus(@RequestBody UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {

        Long driverId = AuthContextHolder.getUserId();
        updateDriverAuthInfoForm.setDriverId(driverId);
        return Result.ok(driverService.updateDriverAuthStatus(updateDriverAuthInfoForm));

    }

    @Operation(summary = "创建配送员人脸模型")
    @PostMapping("/creatDriverFaceModel")
    @PowerLogin
    public Result<Boolean> createDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm) {

        Long driverId = AuthContextHolder.getUserId();
        driverFaceModelForm.setDriverId(driverId);
        return Result.ok(driverService.createDriverFaceModel(driverFaceModelForm));

    }

    @Operation(summary = "查询配送员当天是否进行人脸识别")
    @GetMapping("/isFaceRecognition/")
    @PowerLogin
    public Result<Boolean> isFaceRecognition() {

        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(driverService.isFaceRecognition(driverId));

    }

    @Operation(summary = "配送员人脸识别")
    @PostMapping("/verifyDriverFace")
    @PowerLogin
    public Result<Boolean> verifyDriverFace(@RequestBody DriverFaceModelForm driverFaceModelForm) {

        Long driverId = AuthContextHolder.getUserId();
        driverFaceModelForm.setDriverId(driverId);
        return Result.ok(driverService.verifyDriverFace(driverFaceModelForm));

    }


}

