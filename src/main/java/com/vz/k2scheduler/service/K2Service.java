package com.vz.k2scheduler.service;


import com.vz.k2scheduler.model.ScheduleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class K2Service {

    private static final Logger LOG = LoggerFactory.getLogger(K2Service.class);

    public void triggerExecutionPlan(ScheduleJob scheduleJob) {
        LOG.info(">>>>> jobName = [" + scheduleJob.getJobName() + "]" + " executed.");
        LOG.info("Deployment Id : "+scheduleJob.getDeploymentId());
        LOG.info("BatchType : "+scheduleJob.getBatchType());
        LOG.info("DependsOnBatchType : "+scheduleJob.getDependsOnBatchTypes());



    }
}
