package com.app.theme.listener;
 

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

public class CategoryWriterListener implements StepExecutionListener, JobExecutionListener {

	
	
	
	
    @Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub

    	jobExecution.getExecutionContext().put("categoryMappings", new HashMap<Long, Long>());
		JobExecutionListener.super.beforeJob(jobExecution);
	}



	@Override
    public void beforeStep(StepExecution stepExecution) {
        // Log avant l'exécution de l'étape
        System.out.println("Début de l'étape : " + stepExecution.getStepName());
        stepExecution.getExecutionContext().put("categoryMappings", new HashMap<Long, Long>());
    }

    

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
    	
    	 // Récupérer le contexte d'exécution
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
 
        // Journaliser les données insérées
        if (executionContext.containsKey("categoryMappings")) {
            Map<Long, String> categoryMappings = (Map<Long, String>) executionContext.get("categoryMappings");
            System.out.println("Catégories insérées : " + categoryMappings);
        }
        // Convertir le temps de début en millisecondes
        LocalDateTime startTime = stepExecution.getStartTime();
        long startTimeMillis = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // Temps actuel en millisecondes
        long endTimeMillis = System.currentTimeMillis();

        // Calculer le temps d'exécution
        long executionTime = endTimeMillis - startTimeMillis;
 
        // Afficher des statistiques
        System.out.println("Statistiques de l'étape : " + stepExecution.getStepName());
        System.out.println("Temps d'exécution : " + executionTime + " ms");
        System.out.println("Nombre de lectures : " + stepExecution.getReadCount());
        System.out.println("Nombre d'écritures : " + stepExecution.getWriteCount());
        System.out.println("Nombre de lignes ignorées : " + stepExecution.getSkipCount());
        System.out.println("Nombre d'erreurs : " + stepExecution.getFailureExceptions().size());
        System.out.println("Statut de l'étape : " + stepExecution.getExitStatus().getExitCode());

        return stepExecution.getExitStatus(); // return ExitStatus.COMPLETED;
    }
}