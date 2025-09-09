package com.powernode.dispatch.client;

import com.alibaba.fastjson.JSONObject;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.dispatch.config.XxlJobClientConfig;
import com.powernode.dispatch.hendler.DispatchJobHandler;
import com.powernode.model.entity.dispatch.XxlJobInfo;
import groovy.util.logging.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class XxlJobClient {

    @Resource
    private XxlJobClientConfig xxlJobClientConfig;
    @Resource
    private RestTemplate restTemplate;

    public Long addJob(String executeHandler,String param,String cron,String desc){

        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setJobDesc(desc);
        xxlJobInfo.setJobGroup(xxlJobClientConfig.getJobGroupId());
        xxlJobInfo.setAuthor("321");
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(cron);
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(executeHandler);
        xxlJobInfo.setExecutorParam(param);
        xxlJobInfo.setMisfireStrategy("FIRST");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setMisfireStrategy("FIRE_ONCE_NOW");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> objectHttpEntity = new HttpEntity<>(xxlJobInfo, httpHeaders);
        String url = xxlJobClientConfig.getAddUrl();

        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, objectHttpEntity, JSONObject.class);

        if (response.getStatusCode().value() == 200){
            return response.getBody().getLong("content");
        }

        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);
    }

    public Boolean startJob(Long jobId){

        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(jobId.intValue());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<XxlJobInfo> entity = new HttpEntity<>(xxlJobInfo, httpHeaders);

        String url = xxlJobClientConfig.getStartJobUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, entity, JSONObject.class);

        if (response.getStatusCode().value() == 200){
            return true;
        }
        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);

    }

    public Long addAndStart(String executorHandler, String param, String corn, String desc) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setJobGroup(xxlJobClientConfig.getJobGroupId());
        xxlJobInfo.setJobDesc(desc);
        xxlJobInfo.setAuthor("pat");
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(corn);
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(executorHandler);
        xxlJobInfo.setExecutorParam(param);
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setMisfireStrategy("FIRE_ONCE_NOW");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getAddAndStartUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {

            //content为任务id
            return response.getBody().getLong("content");
        }

        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);
    }

    public Boolean stopJob(Long jobId) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(jobId.intValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getStopJobUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            return true;
        }
        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);
    }

    public Boolean removeJob(Long jobId) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(jobId.intValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getRemoveUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            return true;
        }
        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);
    }
}


