package com.powernode.rules.service.impl;


import com.powernode.model.form.rules.FeeRuleRequest;
import com.powernode.model.form.rules.FeeRuleRequestForm;
import com.powernode.model.vo.rules.FeeRuleResponse;
import com.powernode.model.vo.rules.FeeRuleResponseVo;
import com.powernode.rules.service.FeeRuleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class FeeRuleServiceImpl implements FeeRuleService {

    @Resource
    private KieContainer kieContainer;

    @Override
    public FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm requestForm) {

        FeeRuleRequest feeRuleRequest = new FeeRuleRequest();
        feeRuleRequest.setDistance(requestForm.getDistance());
        feeRuleRequest.setStartTime(new DateTime(requestForm.getStartTime()).toString("HH:mm:ss"));
        feeRuleRequest.setWaitMinute(requestForm.getWaitMinute());

        FeeRuleResponse feeRuleResponse = new FeeRuleResponse();

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("feeRuleResponse", feeRuleResponse);

        kieSession.insert(feeRuleRequest);
        kieSession.fireAllRules();
        kieSession.dispose();

        FeeRuleResponseVo feeRuleResponseVo = new FeeRuleResponseVo();
        BeanUtils.copyProperties(feeRuleResponse, feeRuleResponseVo);

        return feeRuleResponseVo;

    }
}
