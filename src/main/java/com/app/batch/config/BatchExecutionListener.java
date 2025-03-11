package com.app.batch.config;

import java.time.LocalDateTime;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import com.app.batchapp.model.BatchExecution;
import com.app.batchapp.repository.BatchExecutionRepository;

public class BatchExecutionListener extends JobExecutionListenerSupport {
    
    private final BatchExecutionRepository repository;
    private Long executionId;

    public BatchExecutionListener(BatchExecutionRepository repository, Long executionId) {
        this.repository = repository;
        this.executionId = executionId;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        BatchExecution execution = repository.findById(executionId).orElseThrow();
        execution.setEndTime(LocalDateTime.now());
        execution.setStatus(jobExecution.getStatus().toString());
        repository.save(execution);
    }
}