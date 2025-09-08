package com.powernode.driver.service.impl;


import com.alibaba.nacos.common.codec.Base64;
import com.powernode.driver.properties.TencentProperties;
import com.powernode.driver.service.CosService;
import com.powernode.driver.service.OcrService;
import com.powernode.model.vo.driver.CosUploadVo;
import com.powernode.model.vo.driver.IdCardOcrVo;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OcrServiceImpl implements OcrService {

    @Resource
    private TencentProperties tencentProperties;
    @Resource
    private CosService cosService;

    @Override
    public IdCardOcrVo idCardOcr(MultipartFile file){

        try {

            byte[] bytes = Base64.encodeBase64(file.getBytes());
            String s = new String(bytes);

            Credential credential = new Credential(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
            IDCardOCRRequest request = new IDCardOCRRequest();

            request.setImageBase64(s);

            OcrClient ocrClient = new OcrClient(credential, tencentProperties.getRegion());

            IDCardOCRResponse response = ocrClient.IDCardOCR(request);

            IdCardOcrVo idCardOcrVo = new IdCardOcrVo();

            if (StringUtils.hasText(response.getName())){
                idCardOcrVo.setName(response.getName());

                idCardOcrVo.setGender("男".equals(response.getSex())? "1" : "2");
                idCardOcrVo.setBirthday(DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(response.getBirth()).toDate());
                idCardOcrVo.setIdcardNo(response.getIdNum());
                idCardOcrVo.setIdcardAddress(response.getAddress());

                CosUploadVo idCard = cosService.upload(file, "idCard");
                idCardOcrVo.setIdcardFrontUrl(idCard.getUrl());
                idCardOcrVo.setIdcardFrontShowUrl(idCard.getShowUrl());
            }else {
                String idCardExpier = response.getValidDate().split("-")[1];
                Date date = DateTimeFormat.forPattern("yyyy.MM.dd").parseDateTime(idCardExpier).toDate();

                idCardOcrVo.setIdcardExpire(date);

                CosUploadVo idCard = cosService.upload(file, "idCard");
                idCardOcrVo.setIdcardBackUrl(idCard.getUrl());
                idCardOcrVo.setIdcardBackShowUrl(idCard.getShowUrl());
            }
            return idCardOcrVo;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }

    }
}
