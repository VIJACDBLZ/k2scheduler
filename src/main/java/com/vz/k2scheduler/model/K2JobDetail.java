package com.vz.k2scheduler.model;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "k2_job_detail")
public class K2JobDetail{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer jobId;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "job_group", nullable = false)
    private String jobGroup;

    @Column(name = "job_status")
    private String jobStatus;

    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;

    @Column(name = "description")
    private String description;

    @Column(name = "deployment_id", nullable = false)
    private int deploymentId;

    @Column(name = "batch_type", nullable = false)
    private String batchType;

    @Column(name = "depends_on_batchtypes")
    private String dependsOnBatchTypes;

    @Transient
    private K2TriggerDetail k2TriggerDetail = new K2TriggerDetail();

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
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

    public String getDependsOnBatchTypes() {
        return dependsOnBatchTypes;
    }

    public void setDependsOnBatchTypes(String dependsOnBatchTypes) {
        this.dependsOnBatchTypes = dependsOnBatchTypes;
    }

    public K2TriggerDetail getK2TriggerDetail() {
        return k2TriggerDetail;
    }

    public void setK2TriggerDetail(K2TriggerDetail k2TriggerDetail) {
        this.k2TriggerDetail = k2TriggerDetail;
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
                ", triggerDetail=" + k2TriggerDetail +
                '}';
    }
}
