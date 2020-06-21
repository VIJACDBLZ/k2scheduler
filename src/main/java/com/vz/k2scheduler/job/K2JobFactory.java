package com.vz.k2scheduler.job;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import java.util.stream.Collectors;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vz.k2scheduler.model.ScheduleJob;
import com.vz.k2scheduler.service.K2Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import javax.annotation.PostConstruct;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class K2JobFactory implements Job {

    private static Logger log = LogManager.getLogger(K2JobFactory.class.getSimpleName());

    @Autowired
    private K2Service k2Service;


    public static List<ScheduleJob> jobList = Lists.newArrayList();



    public static void loadConfiguration(){
        String jsonConfig;
        try {
            InputStream is = getResourceFileAsInputStream("config.json");
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                jsonConfig = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            } else {
                throw new RuntimeException("config.json not found");
            }

            jobList = new ObjectMapper().readValue(jsonConfig, new TypeReference<List<ScheduleJob>>() {});

        } catch (JsonProcessingException e) {
            log.error("Unable to load the job configuration!");
            e.printStackTrace();
        }

    }


    public static List<ScheduleJob> getAllJobFromConfig() {
        loadConfiguration();
        return jobList;
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        ScheduleJob scheduleJob = (ScheduleJob)jobExecutionContext.getMergedJobDataMap().get("scheduleJob");



        //Validation

        //call k2 execute job
        k2Service.triggerExecutionPlan(scheduleJob);


    }


    public static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = K2JobFactory.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

}
