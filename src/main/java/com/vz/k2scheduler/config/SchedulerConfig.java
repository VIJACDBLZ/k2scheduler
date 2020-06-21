package com.vz.k2scheduler.config;

import com.vz.k2scheduler.job.K2ExecutionJob;
import com.vz.k2scheduler.model.K2JobDetail;
import com.vz.k2scheduler.repository.K2JobDetailRepository;
import com.vz.k2scheduler.spring.AutowiringSpringBeanJobFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.List;
import java.util.Properties;



@Configuration
public class SchedulerConfig {

    private static Logger log = LogManager.getLogger(SchedulerConfig.class.getSimpleName());

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext)
    {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }


    @Bean
    @Primary
    public Scheduler schedulerFactoryBean(JobFactory jobFactory , K2JobDetailRepository k2JobDetailRepository) throws Exception {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // this allows to update triggers in DB when updating settings in config file
        //factory.setOverwriteExistingJobs(true);
        //factory.setDataSource(dataSource);
        // use specify jobFactory to create jobDetail
        factory.setJobFactory(jobFactory);

        factory.setQuartzProperties(quartzProperties());
        factory.afterPropertiesSet();

        Scheduler scheduler = factory.getScheduler();
        scheduler.setJobFactory(jobFactory);

        // register all jobs
        List<K2JobDetail> jobs = k2JobDetailRepository.findAll();
        for (K2JobDetail job : jobs) {

            log.info("Initializing Job :"+ job);

            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (null == trigger) {
                JobDetail jobDetail = JobBuilder.newJob(K2ExecutionJob.class)
                        .withIdentity(job.getJobName(), job.getJobGroup())
                        .withDescription(job.getDescription())
                        .storeDurably(true)
                        .build();
                jobDetail.getJobDataMap().put("scheduleJob", job);
                //Added misfire handling instruction do nothing for ignore nextFires during paused state
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression()).withMisfireHandlingInstructionDoNothing();
                trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();

                scheduler.scheduleJob(jobDetail, trigger);
            }else {
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job
                        .getCronExpression());
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                        .withSchedule(scheduleBuilder).build();
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        }

        scheduler.start();
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
