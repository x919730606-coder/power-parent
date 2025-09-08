package com.powernode.customer.controller;


import com.powernode.common.result.Result;
import com.powernode.customer.service.CustomerInfoService;
import com.powernode.model.entity.customer.CustomerInfo;
import com.powernode.model.form.customer.UpdateWxPhoneForm;
import com.powernode.model.vo.customer.CustomerInfoVo;
import com.powernode.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/customer/info")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerInfoController {

	@Autowired
	private CustomerInfoService customerInfoService;

	@Operation(summary = "获取客户基本信息")
	@GetMapping("/getCustomerInfo/{customerId}")
	public Result<CustomerInfo> getCustomerInfo(@PathVariable Long customerId) {
		return Result.ok(customerInfoService.getById(customerId));
	}

	@Operation(summary = "小程序登录")
	@GetMapping("/login/{code}")
	public Result<Long> login(@PathVariable String code) {
		return Result.ok(customerInfoService.login(code));
	}

	@Operation(summary = "获取登录用户信息")
	@GetMapping("/getCustomerLoginInfo/{customerId}")
	public Result<CustomerLoginVo> getCustomerLoginInfo(@PathVariable Long customerId) {
		return Result.ok(customerInfoService.getCustomerInfo(customerId));
	}

	@Operation(summary = "更新手机号")
	@PostMapping("/updateWxPhoneNumber")
	public Result<Boolean> updatePhone(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {

		return Result.ok(customerInfoService.updatePhone(updateWxPhoneForm));
	}
}

