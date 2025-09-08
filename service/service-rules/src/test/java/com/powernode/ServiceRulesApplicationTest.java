package com.powernode;

import com.powernode.model.form.rules.FeeRuleRequest;
import com.powernode.model.vo.rules.FeeRuleResponse;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class ServiceRulesApplicationTest {

    @Resource
    private KieContainer kieContainer;

    @Test
    public void test() {

        FeeRuleRequest feeRuleRequest = new FeeRuleRequest();
        feeRuleRequest.setDistance(new BigDecimal(10));
        feeRuleRequest.setStartTime("16:00:00");
        feeRuleRequest.setWaitMinute(0);

        KieSession kieSession = kieContainer.newKieSession();

        FeeRuleResponse feeRuleResponse = new FeeRuleResponse();

        kieSession.setGlobal("feeRuleResponse", feeRuleResponse);
        kieSession.insert(feeRuleRequest);

        kieSession.fireAllRules();
        kieSession.dispose();

        System.out.println(feeRuleResponse);

    }
}
