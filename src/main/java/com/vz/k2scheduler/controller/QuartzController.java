package com.vz.k2scheduler.controller;

import com.vz.k2scheduler.model.Message;
import com.vz.k2scheduler.model.ScheduleJob;
import com.vz.k2scheduler.service.QuartzService;


import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quartz")
public class QuartzController {

    private static Logger logger = LoggerFactory.getLogger(QuartzController.class);

    @Autowired
    private QuartzService quartzService;

    @RequestMapping("/getStats")
    public Object getStats() throws SchedulerException {
        return quartzService.getMetaData();
    }

    @RequestMapping("/getAllJobs")
    public Object getAllJobs(){
        return quartzService.getAllJobList();
    }

    @RequestMapping("/getRunningJobs")
    public Object getRunningJobs() throws SchedulerException{
        return quartzService.getRunningJobList();
    }

    @RequestMapping(value = "/pauseJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object pauseJob(@RequestBody ScheduleJob job){
        logger.info("pauseJob, job = {}", job);
        Message message = Message.failure();
        try {
            quartzService.pauseJob(job);
            message = Message.success();
            message.setData(quartzService.getJobById(job));
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("pauseJob ex:", e);
        }
        return message;
    }

    @RequestMapping(value = "/resumeJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object resumeJob(@RequestBody ScheduleJob job){
        logger.info("/resumeJob, job = {}", job);
        Message message = Message.failure();
        try {
            quartzService.resumeJob(job);
            message = Message.success();
            message.setData(quartzService.getJobById(job));
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("resumeJob ex:", e);
        }
        return message;
    }


    @RequestMapping(value = "/deleteJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object deleteJob(@RequestBody ScheduleJob job){
        logger.info("/deleteJob, job = {}", job);
        Message message = Message.failure();
        try {
            quartzService.deleteJob(job);
            message = Message.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("deleteJob ex:", e);
        }
        return message;
    }

    @RequestMapping(value = "/runJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object runJob(@RequestBody ScheduleJob job){
        logger.info("runJob, job = {}", job);
        Message message = Message.failure();
        try {
            quartzService.runJobOnce(job);
            message = Message.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("runJob ex:", e);
        }
        return message;
    }


    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    public Object saveOrupdate(@RequestBody ScheduleJob job){
        logger.info("saveOrUpdate, job = {}", job);
        Message message = Message.failure();
        try {
            quartzService.saveOrupdate(job);
            message = Message.success();
            message.setData(quartzService.getJobById(job));

        } catch (Exception e) {
            message.setMsg(e.getMessage());
            logger.error("updateCron ex:", e);
        }
        return message;
    }


}
