package com.powernode.driver.service.impl;


import com.powernode.driver.client.CiFeignClient;
import com.powernode.driver.service.FileService;
import com.powernode.driver.service.MonitorService;
import com.powernode.model.entity.order.OrderMonitorRecord;
import com.powernode.model.form.order.OrderMonitorForm;
import com.powernode.model.vo.order.TextAuditingVo;
import com.powernode.order.client.OrderMonitorFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class MonitorServiceImpl implements MonitorService {

    @Resource
    private FileService fileService;
    @Resource
    private OrderMonitorFeignClient orderMonitorFeignClient;
    @Resource
    private CiFeignClient ciFeignClient;

    @Override
    public Boolean upload(MultipartFile multipartFile, OrderMonitorForm orderMonitorForm){

        String url = fileService.upload(multipartFile);
        OrderMonitorRecord orderMonitorRecord = new OrderMonitorRecord();

        orderMonitorRecord.setOrderId(orderMonitorForm.getOrderId());
        orderMonitorRecord.setFileUrl(url);
        orderMonitorRecord.setContent(orderMonitorForm.getContent());

        TextAuditingVo textAuditingVo = ciFeignClient.textAuditing(orderMonitorForm.getContent()).getData();
        orderMonitorRecord.setResult(textAuditingVo.getResult());
        orderMonitorRecord.setKeywords(textAuditingVo.getKeywords());

        orderMonitorFeignClient.saveOrderMonitorRecord(orderMonitorRecord);

        return true;

    }


}
