package com.powernode.driver.client;

import com.powernode.common.result.Result;
import com.powernode.model.vo.order.TextAuditingVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "service-driver")
public interface CiFeignClient {

    @PostMapping("/cos/textAuditing")
    Result<TextAuditingVo> textAuditing(String content);


}