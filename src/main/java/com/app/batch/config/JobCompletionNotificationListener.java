package com.app.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {
	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	private long jobId = 0;
	 
	public long getJobId() {
		return jobId;
	}


	public void setJobId(long jobId) {
		this.jobId = jobId;
	}


	@Override
	@BeforeJob
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		JobExecutionListener.super.beforeJob(jobExecution);
		long idjob =jobExecution.getId();
		jobExecution.getExecutionContext().put("jobId", idjob); 
		if(jobExecution.getId() !=null) setJobId(idjob);
		else setJobId(0);
	}


	@Override
	public void afterJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		JobExecutionListener.super.afterJob(jobExecution);
	}


	public ExitStatus afterStep(StepExecution step) {
        long readCount=step.getReadCount();
        long processSkipCount = step.getProcessSkipCount();
        long writeCount=step.getWriteCount();
        long skipCount=step.getSkipCount();
        long commitCount=step.getCommitCount();
        long filterCount=step.getFilterCount();
        long readSkipCount=step.getReadSkipCount();
        long writeSkipCount=step.getWriteSkipCount();
        
        log.info("Step Statistics :\n===================="
        		+ "\n>>>readCount:"+readCount+",writeCount:"+writeCount+",processSkipCount:"+processSkipCount
        		+",readSkipCount:"+readSkipCount+",filterCount:"+filterCount
        		+",skipCount:"+skipCount+",commitCount:"+commitCount
        		+",writeSkipCount:"+writeSkipCount
        		+"\n\nstart:"+step.getStartTime()+",end:"+step.getEndTime()
        	+">>>exitStatus:"+step.getExitStatus().getExitCode()+"\n\n");
        
        return step.getExitStatus();
    }

   
    public void beforeStep(StepExecution arg0) {

    }
    
}