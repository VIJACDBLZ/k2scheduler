package com.vz.k2scheduler.controller;

import com.vz.k2scheduler.model.ScheduleJob;
import com.vz.k2scheduler.service.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private QuartzService quartzService;

    @RequestMapping("/")
    public String showHomePage(Model model){
        List<ScheduleJob> jobList = quartzService.getAllJobList();
        model.addAttribute("jobs", jobList);
        return "index";
    }

    @RequestMapping("/quartz")
    public String index(Model model){
        List<ScheduleJob> jobList = quartzService.getAllJobList();
        model.addAttribute("jobs", jobList);
        return "quartz";
    }

}
