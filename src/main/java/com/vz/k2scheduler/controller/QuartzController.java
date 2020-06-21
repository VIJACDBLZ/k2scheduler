package com.vz.k2scheduler.controller;

import com.vz.k2scheduler.utilities.ResponseWrapper;
import com.vz.k2scheduler.model.K2JobDetail;
import com.vz.k2scheduler.service.QuartzService;


import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public Object pauseJob(@RequestBody K2JobDetail job){
        logger.info("pauseJob, job = {}", job);
        ResponseWrapper responseWrapper = ResponseWrapper.failure();
        try {
            quartzService.pauseJob(job);
            responseWrapper = ResponseWrapper.success();
            responseWrapper.setData(quartzService.getJobById(job));
        } catch (Exception e) {
            responseWrapper.setMsg(e.getMessage());
            logger.error("pauseJob ex:", e);
        }
        return responseWrapper;
    }

    @RequestMapping(value = "/resumeJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object resumeJob(@RequestBody K2JobDetail job){
        logger.info("/resumeJob, job = {}", job);
        ResponseWrapper responseWrapper = ResponseWrapper.failure();
        try {
            quartzService.resumeJob(job);
            responseWrapper = ResponseWrapper.success();
            responseWrapper.setData(quartzService.getJobById(job));
        } catch (Exception e) {
            responseWrapper.setMsg(e.getMessage());
            logger.error("resumeJob ex:", e);
        }
        return responseWrapper;
    }


    @RequestMapping(value = "/deleteJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object deleteJob(@RequestBody K2JobDetail job){
        logger.info("/deleteJob, job = {}", job);
        ResponseWrapper responseWrapper = ResponseWrapper.failure();
        try {
            quartzService.deleteJob(job);
            responseWrapper = ResponseWrapper.success();
        } catch (Exception e) {
            responseWrapper.setMsg(e.getMessage());
            logger.error("deleteJob ex:", e);
        }
        return responseWrapper;
    }

    @RequestMapping(value = "/runJob", method = {RequestMethod.GET, RequestMethod.POST})
    public Object runJob(@RequestBody K2JobDetail job){
        logger.info("runJob, job = {}", job);
        ResponseWrapper responseWrapper = ResponseWrapper.failure();
        try {
            quartzService.runJobOnce(job);
            responseWrapper = ResponseWrapper.success();
        } catch (Exception e) {
            responseWrapper.setMsg(e.getMessage());
            logger.error("runJob ex:", e);
        }
        return responseWrapper;
    }


    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    public Object saveOrupdate(@RequestBody K2JobDetail job){
        logger.info("saveOrUpdate, job = {}", job);
        ResponseWrapper responseWrapper = ResponseWrapper.failure();
        try {
            quartzService.saveOrupdate(job);
            responseWrapper = ResponseWrapper.success();
            responseWrapper.setData(quartzService.getJobById(job));

        } catch (Exception e) {
            responseWrapper.setMsg(e.getMessage());
            logger.error("updateCron ex:", e);
        }
        return responseWrapper;
    }


}
