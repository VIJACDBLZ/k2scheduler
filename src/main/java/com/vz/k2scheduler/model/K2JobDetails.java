package com.vz.k2scheduler.model;

import java.util.Date;

public class K2JobDetails {

    private Date nextTrigger;
    private Date prevTrigger;
    private boolean successfulPreviousRun;

    public Date getNextTrigger() {
        return nextTrigger;
    }

    public void setNextTrigger(Date nextTrigger) {
        this.nextTrigger = nextTrigger;
    }

    public Date getPrevTrigger() {
        return prevTrigger;
    }

    public void setPrevTrigger(Date prevTrigger) {
        this.prevTrigger = prevTrigger;
    }

    public boolean isSuccessfulPreviousRun() {
        return successfulPreviousRun;
    }

    public void setSuccessfulPreviousRun(boolean successfulPreviousRun) {
        this.successfulPreviousRun = successfulPreviousRun;
    }

    @Override
    public String toString() {
        return "K2JobDetails{" +
                "nextTrigger=" + nextTrigger +
                ", prevTrigger=" + prevTrigger +
                ", successfulPreviousRun=" + successfulPreviousRun +
                '}';
    }
}
