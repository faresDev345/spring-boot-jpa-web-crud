package com.app.batch.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Component
public class MyStepListener implements StepExecutionListener {

    @Override
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        // Access JobParameters
        String inputFile = stepExecution.getJobExecution().getJobParameters().getString("inputFile");
        System.out.println("#################  Input file in step: " + inputFile);
    }
 
	private static final Logger log = LoggerFactory.getLogger(MyStepListener.class);
	
	
    @Override
    @AfterStep
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
}