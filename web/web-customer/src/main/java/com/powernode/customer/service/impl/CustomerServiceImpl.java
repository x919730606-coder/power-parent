package com.powernode.customer.service.impl;


import com.powernode.common.constant.RedisConstant;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.Result;
import com.powernode.customer.client.CustomerInfoFeignClient;
import com.powernode.customer.service.CustomerService;
import com.powernode.model.form.customer.UpdateWxPhoneForm;
import com.powernode.model.vo.customer.CustomerLoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomerInfoFeignClient customerInfoFeignClient;

    @Override
    public String login(String code){

        Result<Long> result = customerInfoFeignClient.login(code);

        if (result.getCode() != 200){
            throw new PowerException(result.getCode(), result.getMessage());
        }

        Long customerId = result.getData();

        String token = UUID.randomUUID().toString().replaceAll("-", "");

        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token,
                customerId.toString(),
                RedisConstant.USER_LOGIN_REFRESH_KEY_TIMEOUT,
                java.util.concurrent.TimeUnit.SECONDS);

        return token;
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId){
        Result<CustomerLoginVo> result = customerInfoFeignClient.getCustomerLoginInfo(customerId);

        if (result.getCode() != 200){
            throw new PowerException(result.getCode(), result.getMessage());
        }
        CustomerLoginVo customerLoginVo = result.getData();

        return customerLoginVo;
    }

    @Override
    public Boolean updatePhone(UpdateWxPhoneForm wxPhoneForm){
        customerInfoFeignClient.updatePhone(wxPhoneForm);
        return true;
    }

}
