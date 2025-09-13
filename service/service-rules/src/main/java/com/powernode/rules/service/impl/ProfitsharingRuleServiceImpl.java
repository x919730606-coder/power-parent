package com.powernode.rules.service.impl;


import com.powernode.model.form.rules.ProfitsharingRuleRequest;
import com.powernode.model.form.rules.ProfitsharingRuleRequestForm;
import com.powernode.model.vo.rules.ProfitsharingRuleResponse;
import com.powernode.model.vo.rules.ProfitsharingRuleResponseVo;
import com.powernode.rules.config.DroolsHelper;
import com.powernode.rules.mapper.ProfitsharingRuleMapper;
import com.powernode.rules.service.ProfitsharingRuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProfitsharingRuleServiceImpl implements ProfitsharingRuleService {

    @Autowired
    private ProfitsharingRuleMapper rewardRuleMapper;

    @Override
    public ProfitsharingRuleResponseVo calculateProfitsharingRule(ProfitsharingRuleRequestForm requestForm){

        ProfitsharingRuleRequest request = new ProfitsharingRuleRequest();
        request.setOrderAmount(requestForm.getOrderAmount());
        request.setOrderNum(requestForm.getOrderNum());

        KieSession kieSession = DroolsHelper.loadForRule("rules/ProfitsharingRule.drl");
        ProfitsharingRuleResponse profitsharingRuleResponse = new ProfitsharingRuleResponse();
        kieSession.setGlobal("profitsharingRuleResponse", profitsharingRuleResponse);
        kieSession.insert(request);
        kieSession.fireAllRules();
        kieSession.dispose();

        ProfitsharingRuleResponseVo profitsharingRuleResponseVo = new ProfitsharingRuleResponseVo();
        profitsharingRuleResponseVo.setProfitsharingRuleId(1L);
        BeanUtils.copyProperties(profitsharingRuleResponse, profitsharingRuleResponseVo);

        return profitsharingRuleResponseVo;

    }


}
