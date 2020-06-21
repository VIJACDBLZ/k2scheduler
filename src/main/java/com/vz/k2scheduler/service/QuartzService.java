package com.vz.k2scheduler.service;

import com.google.common.base.Preconditions;
import com.vz.k2scheduler.job.K2JobFactory;
import com.vz.k2scheduler.model.ScheduleJob;
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

    public List<ScheduleJob> getAllJobList(){
        List<ScheduleJob> jobList = new ArrayList<>();
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeySet = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeySet){
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers){
                    ScheduleJob scheduleJob = new ScheduleJob();
                    this.wrapScheduleJob(scheduleJob,scheduler,jobKey,trigger);
                    jobList.add(scheduleJob);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobList;
    }

    public ScheduleJob getJobById(ScheduleJob scheduleJob){

        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());


        try {
            List<? extends Trigger> triggers =  scheduler.getTriggersOfJob(jobKey);
            this.wrapScheduleJob(scheduleJob , scheduler , jobKey , triggers.get(0));

        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return scheduleJob;
    }


    public List<ScheduleJob> getRunningJobList() throws SchedulerException{

        List<JobExecutionContext> executingJobList = scheduler.getCurrentlyExecutingJobs();
        List<ScheduleJob> jobList = new ArrayList<>(executingJobList.size());
        for(JobExecutionContext executingJob : executingJobList){
            ScheduleJob scheduleJob = new ScheduleJob();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            this.wrapScheduleJob(scheduleJob,scheduler,jobKey,trigger);
            jobList.add(scheduleJob);
        }
        return jobList;
    }


    public void saveOrupdate(ScheduleJob scheduleJob) throws Exception {
        String oldCronExpression;
        ScheduleJob oldScheduleJob;


        Preconditions.checkNotNull(scheduleJob, "Job is empty");
        if (StringUtils.isEmpty(scheduleJob.getJobId())) {
            addJob(scheduleJob);
        }else {

            checkNotNull(scheduleJob);
            Preconditions.checkNotNull(scheduleJob.getCronExpression(), "Invalid CronExpression");

            JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            oldScheduleJob = (ScheduleJob) jobDetail.getJobDataMap().get("scheduleJob");

            oldCronExpression = oldScheduleJob.getCronExpression();
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



        }
    }

    private void addJob(ScheduleJob scheduleJob) throws Exception{
        checkNotNull(scheduleJob);
        Preconditions.checkNotNull(scheduleJob.getCronExpression(), "CronExpression is null");

        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if(trigger != null){
            throw new Exception("job already exists!");
        }

        // simulate job info db persist operation
        scheduleJob.setJobId(String.valueOf(K2JobFactory.jobList.size()+1));
        K2JobFactory.jobList.add(scheduleJob);

        JobDetail jobDetail = JobBuilder.newJob(K2JobFactory.class)
                .withIdentity(scheduleJob.getJobName(),scheduleJob.getJobGroup())
                .storeDurably(true)
                .build();

        jobDetail.getJobDataMap().put("scheduleJob", scheduleJob);

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        trigger = TriggerBuilder.newTrigger().withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup()).withSchedule(cronScheduleBuilder).build();

        scheduler.scheduleJob(jobDetail, trigger);

    }


    public void pauseJob(ScheduleJob scheduleJob) throws SchedulerException{
        checkNotNull(scheduleJob);
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.pauseJob(jobKey);
    }

    public void resumeJob(ScheduleJob scheduleJob) throws SchedulerException{
        checkNotNull(scheduleJob);
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.resumeJob(jobKey);
    }

    public void deleteJob(ScheduleJob scheduleJob) throws SchedulerException{
        checkNotNull(scheduleJob);
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.deleteJob(jobKey);
    }

    public void runJobOnce(ScheduleJob scheduleJob) throws SchedulerException{
        checkNotNull(scheduleJob);
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.triggerJob(jobKey);
    }

    private void wrapScheduleJob(ScheduleJob scheduleJob,Scheduler scheduler,JobKey jobKey,Trigger trigger){
        try {
            scheduleJob.setJobName(jobKey.getName());
            scheduleJob.setJobGroup(jobKey.getGroup());

            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            ScheduleJob job = (ScheduleJob)jobDetail.getJobDataMap().get("scheduleJob");
            scheduleJob.setDescription(job.getDescription());
            scheduleJob.setJobId(job.getJobId());
            scheduleJob.setDeploymentId(job.getDeploymentId());
            scheduleJob.setBatchType(job.getBatchType());
            scheduleJob.setDependsOnBatchTypes(job.getDependsOnBatchTypes());


            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            scheduleJob.setJobStatus(triggerState.name());

            scheduleJob.getJobDetails().setNextTrigger(trigger.getNextFireTime());
            scheduleJob.getJobDetails().setPrevTrigger(trigger.getPreviousFireTime());



            if(trigger instanceof CronTrigger){
                CronTrigger cronTrigger = (CronTrigger)trigger;
                String cronExpression = cronTrigger.getCronExpression();
                scheduleJob.setCronExpression(cronExpression);
            }


        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void checkNotNull(ScheduleJob scheduleJob) {
        Preconditions.checkNotNull(scheduleJob, "job is null");
        Preconditions.checkNotNull(scheduleJob.getJobName(), "jobName is null");
        Preconditions.checkNotNull(scheduleJob.getJobGroup(), "jobGroup is null");
    }


    public SchedulerMetaData getMetaData() throws SchedulerException {
        return scheduler.getMetaData();
    }


}