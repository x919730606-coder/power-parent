package com.powernode.driver.service.impl;


import com.powernode.driver.properties.TencentProperties;
import com.powernode.driver.service.CiService;
import com.powernode.model.vo.order.TextAuditingVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ciModel.auditing.*;
import com.qcloud.cos.region.Region;
import jakarta.annotation.Resource;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CiServiceImpl implements CiService {

    @Resource
    private TencentProperties tencentProperties;


    private COSClient getCosClient(){
        COSCredentials cred = new BasicCOSCredentials(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(tencentProperties.getRegion()));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

    @Override
    public Boolean imageAuditing(String path) {

        COSClient cosClient = getCosClient();
        ImageAuditingRequest request = new ImageAuditingRequest();
        request.setBucketName(tencentProperties.getBucketPrivate());
        request.setObjectKey(path);
        cosClient.shutdown();

        ImageAuditingResponse response = cosClient.imageAuditing(request);

        if (!response.getPornInfo().getHitFlag().equals("0")){
            return false;
        }

        return true;

    }

    @Override
    public TextAuditingVo textAuditing(String content) {

        TextAuditingVo textAuditingVo = new TextAuditingVo();
        COSClient cosClient = getCosClient();

        TextAuditingRequest request = new TextAuditingRequest();
        request.setBucketName(tencentProperties.getBucketPrivate());

        byte[] bytes = Base64.encodeBase64(content.getBytes());

        String contentBase64 = new String(bytes);
        request.getInput().setContent(contentBase64);

        request.getConf().setDetectType("all");

        TextAuditingResponse response = cosClient.createAuditingTextJobs(request);
        AuditingJobsDetail detail = response.getJobsDetail();

        if ("success".equalsIgnoreCase(detail.getState())){
            String result = detail.getResult();
            StringBuffer keywords = new StringBuffer();
            List<SectionInfo> sectionList = detail.getSectionList();

            for (SectionInfo info : sectionList) {
                String pronInfoKeyword = info.getPornInfo().getKeywords();
                String AdsInfoKeyword = info.getAdsInfo().getKeywords();

                if (pronInfoKeyword != null && pronInfoKeyword.length() > 0){
                    keywords.append(pronInfoKeyword).append(",");
                }
                if (AdsInfoKeyword != null && AdsInfoKeyword.length() > 0){
                    keywords.append(AdsInfoKeyword).append(",");
                }
            }
            textAuditingVo.setResult(result);
            textAuditingVo.setKeywords(keywords.toString());
        }

        return textAuditingVo;
    }



}
