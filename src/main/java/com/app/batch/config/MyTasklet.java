package com.app.batch.config;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class MyTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        // Access JobParameters
        String inputFile = chunkContext.getStepContext()
                                      .getJobParameters()
                                      .get("inputFile")
                                      .toString();
        System.out.println("Input file in tasklet: " + inputFile);
        return RepeatStatus.FINISHED;
    }
}