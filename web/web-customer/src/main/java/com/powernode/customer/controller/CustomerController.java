package com.powernode.customer.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.constant.RedisConstant;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.customer.service.CustomerService;
import com.powernode.model.form.customer.UpdateWxPhoneForm;
import com.powernode.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomerService customerService;

    @Operation(summary = "小程序登录")
    @GetMapping("/login/{code}")
    public Result<String> login(@PathVariable String code) {
        return Result.ok(customerService.login(code));
    }

    @PowerLogin
    @Operation(summary = "获取用户的登录信息")
    @GetMapping("/getCustomerLoginInfo")
    public Result<CustomerLoginVo> getCustomerLoginInfo(@RequestHeader(value = "token") String token) {
        //String customerId = (String)redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
        Long customerId = AuthContextHolder.getUserId();
        AuthContextHolder.removeUserId();
        return Result.ok(customerService.getCustomerLoginInfo(Long.valueOf(customerId)));
    }

    @Operation(summary = "更新手机号")
    @PostMapping("/updateWxPhone")
    public Result<Boolean> updatePhone(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
        return Result.ok(customerService.updatePhone(updateWxPhoneForm));
    }

}

