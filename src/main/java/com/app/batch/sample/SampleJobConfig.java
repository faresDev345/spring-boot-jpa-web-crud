package com.app.batch.sample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
@Configuration
public class SampleJobConfig {

    @Bean
    public Job sampleJob(JobRepository jobRepository,PlatformTransactionManager transactionManager) {
        return new JobBuilder("sampleJob", jobRepository)
                .start(sampleStep(jobRepository, transactionManager))
                .next(sampleStep2(jobRepository, transactionManager))
                .build();
    }
    @Bean
    public Step sampleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sampleStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
    @Bean
    public Step sampleStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("Step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}