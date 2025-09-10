package com.powernode.driver.service.impl;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.common.constant.SystemConstant;
import com.powernode.driver.mapper.*;
import com.powernode.driver.properties.TencentProperties;
import com.powernode.driver.service.CosService;
import com.powernode.driver.service.DriverInfoService;
import com.powernode.model.entity.driver.*;
import com.powernode.model.form.driver.DriverFaceModelForm;
import com.powernode.model.form.driver.UpdateDriverAuthInfoForm;
import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.iai.v20180301.IaiClient;
import com.tencentcloudapi.iai.v20180301.models.CreatePersonRequest;
import com.tencentcloudapi.iai.v20180301.models.CreatePersonResponse;
import com.tencentcloudapi.iai.v20180301.models.VerifyFaceRequest;
import com.tencentcloudapi.iai.v20180301.models.VerifyFaceResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

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
    @Resource
    private DriverFaceRecognitionMapper driverFaceRecognitionMapper;

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

    @Override
    public DriverSet getDriverSet(Long driverId){

        LambdaQueryWrapper<DriverSet> queryWrapper = new LambdaQueryWrapper<DriverSet>()
                .eq(DriverSet::getDriverId, driverId);
        DriverSet driverSet = driverSetMapper.selectOne(queryWrapper);

        return driverSet;

    }

    @Override
    public Boolean isFaceRecognition(Long driverId){

        LambdaQueryWrapper<DriverFaceRecognition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DriverFaceRecognition::getDriverId, driverId);
        queryWrapper.eq(DriverFaceRecognition::getFaceDate, new DateTime().toString("yyyy-MM-dd"));

        Long count = driverFaceRecognitionMapper.selectCount(queryWrapper);

        return count != 0;

    }

    @Override
    public Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm){

        try {

            Credential credential = new Credential(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            IaiClient iaiClient = new IaiClient(credential, tencentProperties.getRegion(), clientProfile);

            VerifyFaceRequest verifyFaceRequest = new VerifyFaceRequest();

            verifyFaceRequest.setImage(driverFaceModelForm.getImageBase64());
            verifyFaceRequest.setPersonId(driverFaceModelForm.getDriverId().toString());

            VerifyFaceResponse verifyFaceResponse = iaiClient.VerifyFace(verifyFaceRequest);

            if (verifyFaceResponse.getIsMatch()){

                DriverFaceRecognition driverFaceRecognition = new DriverFaceRecognition();
                driverFaceRecognition.setDriverId(driverFaceModelForm.getDriverId());
                driverFaceRecognition.setFaceDate(new Date());

                driverFaceRecognitionMapper.insert(driverFaceRecognition);
                return true;

            }

        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }

        return false;

    }

    @Override
    public Boolean updateServiceStatus(Long driverId, Integer status) {

        LambdaQueryWrapper<DriverSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DriverSet::getDriverId, driverId);

        DriverSet driverSet = new DriverSet();
        driverSet.setServiceStatus(status);

        driverSetMapper.update(driverSet, queryWrapper);

        return true;

    }

}