package com.app.batch.config;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;
@Component
public class BatchListener implements JobExecutionListener,JobExecutionDecider {

	private long totalRecord = 0;

	public BatchListener() {
		super();
	}

	public BatchListener(long totalRecord) {
		super();
		this.totalRecord = totalRecord;
	}

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
  public void checkedStatusJob(JobExecution jobExecution) {
    	  System.out.println("Status of the Job: "+jobExecution.getStatus());
    	    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
    			System.out.println("Job Ended at: " + jobExecution.getEndTime());
    		} else {
    			System.out.println("Job ID : " + jobExecution.getId());
    			System.out.println("Job Execution Failed with Status : " + jobExecution.getStatus());
    			jobExecution.setExitStatus(new ExitStatus("STOPEED", "INTERRUPTION STATUS"));
    		}
      	
   
    }
	@Override
	public void beforeJob(JobExecution jobExecution) {

		System.out.println("Job started at: " + jobExecution.getStartTime());
		System.out.println("Status of the Job: " + jobExecution.getStatus());
		checkedStatusJob(jobExecution) ;

	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("Status of the Job: " + jobExecution.getStatus());
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("Job Ended at: " + jobExecution.getEndTime());
			System.out.println("total of records: " + totalRecord);
		} else {
			System.out.println("Job Execution Failed with Status : " + jobExecution.getStatus());
		}

	}

	long countTotalRecord(JobExecution jobExecution, StepExecution stepExecution) {
		
		return 0;	
	}
	
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		String status ;
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("Job Ended at: " + jobExecution.getEndTime());
			System.out.println("total of records: " + totalRecord);
		} else {
			System.out.println("Job Execution Failed with Status : " + jobExecution.getStatus());
		}
		
		return null;
	}
	
	

}
