package com.powernode.driver.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.driver.properties.TencentProperties;
import com.powernode.driver.service.CiService;
import com.powernode.driver.service.CosService;
import com.powernode.model.vo.driver.CosUploadVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;


@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosServiceImpl implements CosService {

    @Resource
    private TencentProperties tencentProperties;
    @Resource
    private CiService ciService;

    @Override
    public CosUploadVo upload(MultipartFile file, String path){

        COSClient cosClient = getCosClient();
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentEncoding("utf-8");

        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        String uploadPath = "/driver/" + path + "/" + UUID.randomUUID().toString().replaceAll("-", "") + fileType;

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(tencentProperties.getBucketPrivate(), uploadPath, file.getInputStream(), objectMetadata);
            putObjectRequest.setStorageClass(StorageClass.Standard);

            cosClient.putObject(putObjectRequest);
            cosClient.shutdown();

            Boolean flag = ciService.imageAuditing(uploadPath);

            if (!flag){
                cosClient.deleteObject(tencentProperties.getBucketPrivate(), uploadPath);
                throw new PowerException(ResultCodeEnum.IMAGE_AUDITION_FAIL);
            }

            CosUploadVo cosUploadVo = new CosUploadVo();
            cosUploadVo.setUrl(uploadPath);

            cosUploadVo.setShowUrl(getImgUrl(path));

            return cosUploadVo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getImgUrl(String path) {
        COSClient cosClient = getCosClient();

        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(tencentProperties.getBucketPrivate(), path, HttpMethodName.GET);
        Date expiration = new DateTime().plusMillis(20).toDate();
        urlRequest.setExpiration(expiration);

        URL url = cosClient.generatePresignedUrl(urlRequest);
        cosClient.shutdown();


        return url.toString();
    }

    private COSClient getCosClient() {
        COSCredentials cred = new BasicCOSCredentials(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
        Region region = new Region(tencentProperties.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

}
