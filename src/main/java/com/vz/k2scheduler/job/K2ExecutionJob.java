package com.vz.k2scheduler.job;


import com.vz.k2scheduler.model.K2ExecutionDetail;
import com.vz.k2scheduler.model.K2JobDetail;
import com.vz.k2scheduler.repository.K2ExecutionDetailRepository;
import com.vz.k2scheduler.service.K2Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class K2ExecutionJob implements Job {

    private static Logger log = LogManager.getLogger(K2ExecutionJob.class.getSimpleName());

    @Autowired
    private K2Service k2Service;

    @Autowired
    private K2ExecutionDetailRepository executionDetailRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        K2JobDetail k2JobDetail = (K2JobDetail)jobExecutionContext.getMergedJobDataMap().get("scheduleJob");

        K2ExecutionDetail executionDetail = new K2ExecutionDetail();
        setPropertiesForExecutionDetail(executionDetail , k2JobDetail);


        //Validation

        //call k2 execute job
        try {
            k2Service.triggerExecutionPlan(k2JobDetail);

            //TODO: get from response and set executionID
            executionDetail.setExecutionId("1212");
            executionDetail.setStatus("SUCCESS");

        }catch (Exception e){
            String message = e.getMessage();
            executionDetail.setStatus("FAILED");
            executionDetail.setErrorMessage(message);
        }

        executionDetailRepository.save(executionDetail);
    }


    private void setPropertiesForExecutionDetail(K2ExecutionDetail executionDetail , K2JobDetail k2JobDetail){
        executionDetail.setJobId(Long.valueOf(k2JobDetail.getJobId()));
        executionDetail.setJobName(k2JobDetail.getJobName());
        executionDetail.setJobGroup(k2JobDetail.getJobGroup());
        executionDetail.setBatchType(k2JobDetail.getBatchType());
        executionDetail.setDependsOnBatchTypes(k2JobDetail.getDependsOnBatchTypes());
        executionDetail.setDeploymentId(String.valueOf(k2JobDetail.getDeploymentId()));
        executionDetail.setExecutedAt(new Date());

    }

}
