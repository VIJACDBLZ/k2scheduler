package com.vz.k2scheduler.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScheduleJob implements Serializable{

    private static final long serialVersionUID = 1L;

    private String jobId;

    private String jobName;

    private String jobGroup;

    private String jobStatus;

    private String cronExpression;

    private String description;

    private int deploymentId;

    private String batchType;

    private List<String> dependsOnBatchTypes = new ArrayList<>();

    private K2JobDetails jobDetails = new K2JobDetails();

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(int deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    public List<String> getDependsOnBatchTypes() {
        return dependsOnBatchTypes;
    }

    public void setDependsOnBatchTypes(List<String> dependsOnBatchTypes) {
        this.dependsOnBatchTypes = dependsOnBatchTypes;
    }

    public K2JobDetails getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(K2JobDetails jobDetails) {
        this.jobDetails = jobDetails;
    }

    @Override
    public String toString() {
        return "ScheduleJob{" +
                "jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", description='" + description + '\'' +
                ", deploymentId=" + deploymentId +
                ", batchType='" + batchType + '\'' +
                ", dependsOnBatchTypes=" + dependsOnBatchTypes +
                ", jobDetails=" + jobDetails +
                '}';
    }
}
