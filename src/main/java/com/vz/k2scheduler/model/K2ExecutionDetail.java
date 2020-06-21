package com.vz.k2scheduler.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "k2_execution_detail")
public class K2ExecutionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer executionDetailId;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "job_group", nullable = false)
    private String jobGroup;

    @Column(name = "batch_type")
    private String batchType;

    @Column(name = "depends_on_batchtypes")
    private String dependsOnBatchTypes;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "deployment_id")
    private String deploymentId;

    @Column(name = "execution_id")
    private String executionId;

    @Column(name = "execution_at")
    private Date executedAt;

    public Integer getExecutionDetailId() {
        return executionDetailId;
    }

    public void setExecutionDetailId(Integer executionDetailId) {
        this.executionDetailId = executionDetailId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public Date getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Date executedAt) {
        this.executedAt = executedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
