package com.app.article.listener;
 

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

public class ArticleWriterListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // Log avant l'exécution de l'étape
        System.out.println("Début de l'étape : " + stepExecution.getStepName()); 
    }

    

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
    	
    	 // Récupérer le contexte d'exécution
        ExecutionContext executionContext = stepExecution.getExecutionContext();
 
        // Journaliser les données insérées
       
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