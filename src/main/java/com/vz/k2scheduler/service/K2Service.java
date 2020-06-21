package com.vz.k2scheduler.service;


import com.vz.k2scheduler.model.K2JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class K2Service {

    private static final Logger LOG = LoggerFactory.getLogger(K2Service.class);

    public void triggerExecutionPlan(K2JobDetail k2JobDetail) {
        LOG.info(">>>>> jobName = [" + k2JobDetail.getJobName() + "]" + " executed.");
        LOG.info("Deployment Id : "+ k2JobDetail.getDeploymentId());
        LOG.info("BatchType : "+ k2JobDetail.getBatchType());
        LOG.info("DependsOnBatchType : "+ k2JobDetail.getDependsOnBatchTypes());



    }
}
