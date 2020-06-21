package com.vz.k2scheduler.service;

import com.google.common.base.Preconditions;
import com.vz.k2scheduler.job.K2ExecutionJob;
import com.vz.k2scheduler.model.K2JobDetail;
import com.vz.k2scheduler.repository.K2JobDetailRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class QuartzService {

    private static Logger LOG = LogManager.getLogger(QuartzService.class.getSimpleName());

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private K2JobDetailRepository k2JobDetailRepository;

    public List<K2JobDetail> getAllJobList(){
        List<K2JobDetail> jobList = new ArrayList<>();
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeySet = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeySet){
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers){
                    K2JobDetail k2JobDetail = new K2JobDetail();
                    this.wrapScheduleJob(k2JobDetail,scheduler,jobKey,trigger);
                    jobList.add(k2JobDetail);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobList;
    }

    public K2JobDetail getJobById(K2JobDetail k2JobDetail){

        JobKey jobKey = JobKey.jobKey(k2JobDetail.getJobName(), k2JobDetail.getJobGroup());


        try {
            List<? extends Trigger> triggers =  scheduler.getTriggersOfJob(jobKey);
            this.wrapScheduleJob(k2JobDetail, scheduler , jobKey , triggers.get(0));

        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return k2JobDetail;
    }


    public List<K2JobDetail> getRunningJobList() throws SchedulerException{

        List<JobExecutionContext> executingJobList = scheduler.getCurrentlyExecutingJobs();
        List<K2JobDetail> jobList = new ArrayList<>(executingJobList.size());
        for(JobExecutionContext executingJob : executingJobList){
            K2JobDetail scheduleJob = new K2JobDetail();
            org.quartz.JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            this.wrapScheduleJob(scheduleJob,scheduler,jobKey,trigger);
            jobList.add(scheduleJob);
        }
        return jobList;
    }


    public void saveOrupdate(K2JobDetail scheduleJob) throws Exception {
        String oldCronExpression;
        K2JobDetail oldK2JobDetail;


        Preconditions.checkNotNull(scheduleJob, "Job is empty");
        if (StringUtils.isEmpty(scheduleJob.getJobId())) {
            addJob(scheduleJob);
        }else {

            checkNotNull(scheduleJob);
            Preconditions.checkNotNull(scheduleJob.getCronExpression(), "Invalid CronExpression");

            JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            oldK2JobDetail = (K2JobDetail) jobDetail.getJobDataMap().get("scheduleJob");

            oldCronExpression = oldK2JobDetail.getCronExpression();
            LOG.info("Old cron expression : "+ oldCronExpression);
            LOG.info("New cron expression : "+ scheduleJob.getCronExpression());

            if(!oldCronExpression.equals(scheduleJob.getCronExpression())){

                LOG.info("Updating cron expression and rescheduling");

                TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
                CronTrigger cronTrigger = (CronTrigger)scheduler.getTrigger(triggerKey);
                CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
                cronTrigger = cronTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
                scheduler.rescheduleJob(triggerKey, cronTrigger);
            }else {
                LOG.info("Not rescheduling as cron expression is still the same!");
            }


            jobDetail.getJobDataMap().put("scheduleJob", scheduleJob);
            scheduler.addJob(jobDetail, true);

            k2JobDetailRepository.save(scheduleJob);
        }
    }

    private void addJob(K2JobDetail scheduleJob) throws Exception{
        checkNotNull(scheduleJob);
        Preconditions.checkNotNull(scheduleJob.getCronExpression(), "CronExpression is null");

        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if(trigger != null){
            throw new Exception("job already exists!");
        }

        k2JobDetailRepository.save(scheduleJob);

        JobDetail jobDetail = JobBuilder.newJob(K2ExecutionJob.class)
                .withIdentity(scheduleJob.getJobName(),scheduleJob.getJobGroup())
                .storeDurably(true)
                .build();

        jobDetail.getJobDataMap().put("scheduleJob", scheduleJob);

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        trigger = TriggerBuilder.newTrigger().withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup()).withSchedule(cronScheduleBuilder).build();

        scheduler.scheduleJob(jobDetail, trigger);

    }


    public void pauseJob(K2JobDetail k2JobDetail) throws SchedulerException{
        checkNotNull(k2JobDetail);
        JobKey jobKey = JobKey.jobKey(k2JobDetail.getJobName(), k2JobDetail.getJobGroup());
        scheduler.pauseJob(jobKey);
    }

    public void resumeJob(K2JobDetail k2JobDetail) throws SchedulerException{
        checkNotNull(k2JobDetail);
        JobKey jobKey = JobKey.jobKey(k2JobDetail.getJobName(), k2JobDetail.getJobGroup());
        scheduler.resumeJob(jobKey);
    }

    public void deleteJob(K2JobDetail k2JobDetail) throws SchedulerException{
        checkNotNull(k2JobDetail);
        JobKey jobKey = JobKey.jobKey(k2JobDetail.getJobName(), k2JobDetail.getJobGroup());
        scheduler.deleteJob(jobKey);
    }

    public void runJobOnce(K2JobDetail k2JobDetail) throws SchedulerException{
        checkNotNull(k2JobDetail);
        JobKey jobKey = JobKey.jobKey(k2JobDetail.getJobName(), k2JobDetail.getJobGroup());
        scheduler.triggerJob(jobKey);
    }

    private void wrapScheduleJob(K2JobDetail scheduleJob, Scheduler scheduler, JobKey jobKey, Trigger trigger){
        try {
            scheduleJob.setJobName(jobKey.getName());
            scheduleJob.setJobGroup(jobKey.getGroup());

            org.quartz.JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            K2JobDetail job = (K2JobDetail)jobDetail.getJobDataMap().get("scheduleJob");
            scheduleJob.setDescription(job.getDescription());
            scheduleJob.setJobId(job.getJobId());
            scheduleJob.setDeploymentId(job.getDeploymentId());
            scheduleJob.setBatchType(job.getBatchType());
            scheduleJob.setDependsOnBatchTypes(job.getDependsOnBatchTypes());


            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            scheduleJob.setJobStatus(triggerState.name());

            scheduleJob.getK2TriggerDetail().setNextTrigger(trigger.getNextFireTime());
            scheduleJob.getK2TriggerDetail().setPrevTrigger(trigger.getPreviousFireTime());



            if(trigger instanceof CronTrigger){
                CronTrigger cronTrigger = (CronTrigger)trigger;
                String cronExpression = cronTrigger.getCronExpression();
                scheduleJob.setCronExpression(cronExpression);
            }


        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void checkNotNull(K2JobDetail k2JobDetail) {
        Preconditions.checkNotNull(k2JobDetail, "job is null");
        Preconditions.checkNotNull(k2JobDetail.getJobName(), "jobName is null");
        Preconditions.checkNotNull(k2JobDetail.getJobGroup(), "jobGroup is null");
    }


    public SchedulerMetaData getMetaData() throws SchedulerException {
        return scheduler.getMetaData();
    }


}