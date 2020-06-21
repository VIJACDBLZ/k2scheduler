package com.vz.k2scheduler.config;

import org.quartz.impl.triggers.CronTriggerImpl;

import java.util.Date;

public class PauseAwareCronTrigger extends CronTriggerImpl
{
    private static final long serialVersionUID = -2609414523458705574L;

    public Date getNextFireTime()
	{
		Date nextFireTime = super.getNextFireTime();
		if(nextFireTime.getTime() < System.currentTimeMillis())
		{
			nextFireTime = super.getFireTimeAfter(null);
			super.setNextFireTime(nextFireTime);
		}

		return nextFireTime;
	}
}