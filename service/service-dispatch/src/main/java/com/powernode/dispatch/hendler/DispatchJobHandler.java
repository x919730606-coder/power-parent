package com.powernode.dispatch.hendler;

import com.xxl.job.core.handler.annotation.XxlJob;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DispatchJobHandler {

    private static final Logger log = LoggerFactory.getLogger(DispatchJobHandler.class);

    @XxlJob("firstJobHandle")
    public void firstJpbHandle(String jobId) {

        log.info("firstJobHandle任务执行");

    }

}
