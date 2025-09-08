package com.powernode.customer.service.impl;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.customer.mapper.CustomerInfoMapper;
import com.powernode.customer.mapper.CustomerLoginLogMapper;
import com.powernode.customer.service.CustomerInfoService;
import com.powernode.model.entity.customer.CustomerInfo;
import com.powernode.model.entity.customer.CustomerLoginLog;
import com.powernode.model.form.customer.UpdateWxPhoneForm;
import com.powernode.model.vo.customer.CustomerLoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {

    @Resource
    private WxMaService wxMaService;
    @Resource
    private CustomerLoginLogMapper customerLoginLogMapper;

    @Transactional
    @Override
    public Long login(String code) {

        try {

            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            String openid = sessionInfo.getOpenid();

            LambdaQueryWrapper<CustomerInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CustomerInfo::getWxOpenId, openid);
            CustomerInfo customerInfo = getOne(queryWrapper);

            if (customerInfo == null){

                customerInfo = new CustomerInfo();
                customerInfo.setWxOpenId(openid);
                customerInfo.setStatus(1);
                customerInfo.setNickname("微信用户");
                customerInfo.setAvatarUrl("https://wx.qlogo.cn/");
                customerInfo.setCreateTime(new Date());

                save(customerInfo);
            }

            CustomerLoginLog customerLoginLog = new CustomerLoginLog();
            customerLoginLog.setCustomerId(customerInfo.getId());
            customerLoginLog.setMsg("小程序登录");
            customerLoginLogMapper.insert(customerLoginLog);

            return customerInfo.getId();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public CustomerLoginVo getCustomerInfo(Long customerId) {

        CustomerInfo customerInfo = getById(customerId);
        CustomerLoginVo customerLoginVo = new CustomerLoginVo();
        BeanUtils.copyProperties(customerInfo, customerLoginVo);
        boolean b = StringUtils.hasText(customerInfo.getPhone());
        customerLoginVo.setIsBindPhone(b);

        return customerLoginVo;

    }

    @Override
    public Boolean updatePhone(UpdateWxPhoneForm wxPhoneForm){
        try {
            WxMaPhoneNumberInfo phoneNumberInfo = wxMaService.getUserService().getPhoneNoInfo(wxPhoneForm.getCode());
            String phone = phoneNumberInfo.getPhoneNumber();

            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setId(wxPhoneForm.getCustomerId());
            customerInfo.setPhone(phone);

            return updateById(customerInfo);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
