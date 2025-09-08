package com.powernode.driver.service.impl;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.common.constant.SystemConstant;
import com.powernode.driver.mapper.DriverAccountMapper;
import com.powernode.driver.mapper.DriverInfoMapper;
import com.powernode.driver.mapper.DriverLoginLogMapper;
import com.powernode.driver.mapper.DriverSetMapper;
import com.powernode.driver.properties.TencentProperties;
import com.powernode.driver.service.CosService;
import com.powernode.driver.service.DriverInfoService;
import com.powernode.model.entity.driver.DriverAccount;
import com.powernode.model.entity.driver.DriverInfo;
import com.powernode.model.entity.driver.DriverLoginLog;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.form.driver.DriverFaceModelForm;
import com.powernode.model.form.driver.UpdateDriverAuthInfoForm;
import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.iai.v20180301.IaiClient;
import com.tencentcloudapi.iai.v20180301.models.CreatePersonRequest;
import com.tencentcloudapi.iai.v20180301.models.CreatePersonResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverInfoService {

    @Resource
    private WxMaService wxMaService;
    @Resource
    private DriverSetMapper driverSetMapper;
    @Resource
    private DriverAccountMapper driverAccountMapper;
    @Resource
    private DriverLoginLogMapper driverLoginLogMapper;
    @Resource
    private CosService cosService;
    @Resource
    private TencentProperties tencentProperties;

    @Override
    public Long login(String code) {

        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            String openid = sessionInfo.getOpenid();

            LambdaQueryWrapper<DriverInfo> queryWrapper = new LambdaQueryWrapper<>();

            DriverInfo customerInfo = getOne(queryWrapper);

            if (customerInfo == null){
                customerInfo = new DriverInfo();
                customerInfo.setWxOpenId(openid);
                customerInfo.setStatus(1);
                save(customerInfo);
            }

            DriverSet driverSet = new DriverSet();
            driverSet.setDriverId(customerInfo.getId());
            driverSet.setServiceStatus(0);
            driverSet.setAcceptDistance(new BigDecimal(SystemConstant.ACCEPT_DISTANCE));
            driverSet.setOrderDistance(BigDecimal.ZERO);
            driverSet.setIsAutoAccept(0);
            int insert = driverSetMapper.insert(driverSet);

            DriverAccount driverAccount = new DriverAccount();
            driverAccount.setDriverId(customerInfo.getId());
            driverAccountMapper.insert(driverAccount);

            DriverLoginLog customerLoginLog = new DriverLoginLog();
            customerLoginLog.setDriverId(customerInfo.getId());
            customerLoginLog.setMsg("登录成功");
            customerLoginLog.setStatus(true);
            driverLoginLogMapper.insert(customerLoginLog);

            return customerInfo.getId();

        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public DriverLoginVo getDriverLoginInfo(Long driverId) {

        DriverInfo driverInfo = getById(driverId);
        DriverLoginVo driverLoginVo = new DriverLoginVo();
        BeanUtils.copyProperties(driverInfo,driverLoginVo);

        boolean isFace = StringUtils.hasText(driverInfo.getFaceModelId());
        driverLoginVo.setIsArchiveFace(isFace);

        return driverLoginVo;
    }

    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {

        DriverInfo driverInfo = getById(driverId);
        DriverAuthInfoVo driverAuthInfoVo = new DriverAuthInfoVo();
        BeanUtils.copyProperties(driverInfo,driverAuthInfoVo);

        driverAuthInfoVo.setDriverId(driverId);
        driverAuthInfoVo.setIdcardFrontShowUrl(cosService.getImgUrl(driverAuthInfoVo.getIdcardFrontUrl()));
        driverAuthInfoVo.setIdcardBackShowUrl(cosService.getImgUrl(driverAuthInfoVo.getIdcardBackUrl()));
        driverAuthInfoVo.setDriverLicenseFrontShowUrl(cosService.getImgUrl(driverAuthInfoVo.getDriverLicenseFrontUrl()));

        return driverAuthInfoVo;
    }

    @Override
    public Boolean updateDriverAuthStatus(UpdateDriverAuthInfoForm driverAuthInfoForm) {

        DriverInfo driverInfo = new DriverInfo();
        BeanUtils.copyProperties(driverAuthInfoForm,driverInfo);
        driverInfo.setId(driverAuthInfoForm.getDriverId());

        return updateById(driverInfo);
    }

    @Override
    public Boolean createDriverFaceModel(DriverFaceModelForm driverFaceModelForm){

        DriverInfo driverInfo = getById(driverFaceModelForm.getDriverId());

        Credential credential = new Credential(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
        CreatePersonRequest request = new CreatePersonRequest();
        request.setGroupId(tencentProperties.getPersionGroupId());
        request.setPersonId(driverInfo.getId().toString());
        request.setGender(Long.parseLong(driverInfo.getGender()));
        request.setQualityControl(4L);
        request.setUniquePersonControl(4L);
        request.setPersonName(driverInfo.getName());
        request.setImage(driverFaceModelForm.getImageBase64());

        IaiClient iaiClient = new IaiClient(credential, tencentProperties.getRegion());

        try {
            CreatePersonResponse response = iaiClient.CreatePerson(request);
            String faceId = response.getFaceId();

            if (StringUtils.hasText(faceId)){
                driverInfo.setFaceModelId(faceId);
                updateById(driverInfo);
            }

            return true;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }
}