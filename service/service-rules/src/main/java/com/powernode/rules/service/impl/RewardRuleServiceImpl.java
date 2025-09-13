package com.powernode.rules.service.impl;


import com.powernode.model.form.rules.RewardRuleRequest;
import com.powernode.model.form.rules.RewardRuleRequestForm;
import com.powernode.model.vo.rules.RewardRuleResponse;
import com.powernode.model.vo.rules.RewardRuleResponseVo;
import com.powernode.rules.config.DroolsHelper;
import com.powernode.rules.service.RewardRuleService;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class RewardRuleServiceImpl implements RewardRuleService {

    @Override
    public RewardRuleResponseVo calculateOrderRewardFee(RewardRuleRequestForm requestForm) {

        RewardRuleRequest request = new RewardRuleRequest();
        request.setOrderNum(requestForm.getOrderNum());

        KieSession kieSession = DroolsHelper.loadForRule("rules/RewardRule.drl");

        RewardRuleResponse rewardRuleResponse = new RewardRuleResponse();
        kieSession.setGlobal("rewardRuleResponse", rewardRuleResponse);

        kieSession.insert(request);

        kieSession.fireAllRules();

        kieSession.dispose();

        RewardRuleResponseVo rewardRuleResponseVo = new RewardRuleResponseVo();
        rewardRuleResponseVo.setRewardAmount(rewardRuleResponse.getRewardAmount());
        rewardRuleResponseVo.setRewardRuleId(1L);

        return rewardRuleResponseVo;

    }
}
