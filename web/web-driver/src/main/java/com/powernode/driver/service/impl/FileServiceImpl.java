package com.powernode.driver.service.impl;


import com.powernode.driver.properties.MinioProperties;
import com.powernode.driver.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class FileServiceImpl implements FileService {

    @Resource
    private MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile multipartFile){

        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpointUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecreKey())
                .build();

        String extFileName = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));

        String fileName = new SimpleDateFormat("yyyyMMdd")
                .format(new Date() + "/" + UUID.randomUUID()
                        .toString()
                        .replaceAll("-", "") + extFileName);

        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(fileName)
                    .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                    .contentType(multipartFile.getContentType())
                    .build();
            minioClient.putObject(putObjectArgs);

            return minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
