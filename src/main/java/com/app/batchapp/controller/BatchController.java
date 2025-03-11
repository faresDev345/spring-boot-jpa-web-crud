package com.app.batchapp.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.batchapp.dto.JobBatchForm;
import com.app.batchapp.model.BatchExecution;
import com.app.batchapp.model.BatchFile;
import com.app.batchapp.model.JobStatus;
import com.app.batchapp.repository.BatchExecutionRepository;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class BatchController {
	  @Autowired
    private  BatchExecutionRepository repository;
    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;
    @Autowired
    private List<Job> jobs;

	@Autowired
	private ApplicationContext context;

	  @Autowired
	    private JobRegistry jobRegistry;

	    @GetMapping("/jobs")
	    public String getJobs(Model model)  {
	        List<JobBatchForm> jobInfos = new ArrayList<>();
	        for (Job job : jobs) {
	        	JobBatchForm jobInfo = new JobBatchForm();
	        	 jobInfo.setCode(job.getName());
	        	 jobInfo.setName(job.getName());
	            // Ajouter d'autres informations sur le job si nécessaire
	        	 
	             jobInfo.setStatus(getJobStatus(job)); // Récupérer le statut du job
	             jobInfo.setAvailable(isRestartable(jobInfo)); // Déterminer si le job peut être relancé
	             jobInfos.add(jobInfo);
	            jobInfos.add(jobInfo);
	        }
	        model.addAttribute("jobs", jobInfos); 
	        return "jobs";
	    }
	    
	    @GetMapping("/lstJobs")
	    public List<JobBatchForm> getJobs() {
	    	 List<JobBatchForm> jobInfos = new ArrayList<>();
		        for (Job job : jobs) {
		        	JobBatchForm jobInfo = new JobBatchForm();
		        	 jobInfo.setCode(job.getName());
		        	 jobInfo.setName(job.getName());
		            // Ajouter d'autres informations sur le job si nécessaire
		        	 
		             jobInfo.setStatus(getJobStatus(job)); // Récupérer le statut du job
		             jobInfo.setAvailable(isRestartable(jobInfo)); // Déterminer si le job peut être relancé
		             jobInfos.add(jobInfo);
		            jobInfos.add(jobInfo);
		        } 
	        return jobInfos;
	    }

	    
	    private JobStatus getJobStatus(Job job) {
	        List<JobInstance> jobInstances =  jobExplorer.findJobInstancesByJobName(job.getName(), 0, Integer.MAX_VALUE);
	        if (jobInstances.isEmpty()) {
	        	 return JobStatus.valueOf("UNKNOWN");  // Aucun instance de job trouvée
	        }

	        JobInstance lastJobInstance = jobInstances.get(0); // Récupérer la dernière instance
	        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(lastJobInstance);
	        if (jobExecutions.isEmpty()) {
	            return JobStatus.valueOf("UNKNOWN"); // Aucune exécution de job trouvée
	        }

	        JobExecution lastJobExecution = jobExecutions.get(0); // Récupérer la dernière exécution
	        return  JobStatus.valueOf(lastJobExecution.getStatus().toString());
	    }

	  

	public void runJob(String jobName, JobParameters jbParam) {
		try {
			// Récupère le job spécifié depuis le contexte en utilisant le nom du job
			Job job = context.getBean(jobName, Job.class);
			jobLauncher.run(job,jbParam);
					//new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters());
			System.out.println("Job " + jobName + " lancé avec succès.");
		} catch (Exception e) {
			System.err.println("Erreur lors de l'exécution du job " + jobName + ": " + e.getMessage());
		}
	}
	  private boolean isRestartable(JobBatchForm job) {
	        // Logique pour déterminer si le job peut être relancé (par exemple, en fonction de son statut précédent)
	        return job.getStatus().equals(JobStatus.FAILED) && (job.getStatus().equals(JobStatus.COMPLETED) ); // À adapter selon vos besoins
	    }
	  
	  private boolean isRestartable(Job job) {
		    List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName(job.getName(), 0, Integer.MAX_VALUE);
		    if (jobInstances.isEmpty()) {
		        return false; // Aucune instance, le job n'a jamais été exécuté
		    }

		    JobInstance lastJobInstance = jobInstances.get(0); // Dernière instance
		    List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(lastJobInstance);
		    if (jobExecutions.isEmpty()) {
		        return false; // Aucune exécution pour cette instance
		    }

		    JobExecution lastJobExecution = jobExecutions.get(0);
		    BatchStatus status = lastJobExecution.getExitStatus().getExitCode().equals("COMPLETED") ? BatchStatus.COMPLETED : lastJobExecution.getStatus(); // statut de l'exit status ou du job execution
		    return status == BatchStatus.FAILED || status == BatchStatus.COMPLETED;
		}

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("jobs", List.of("IMPORT_CSV_JOB", "EXPORT_DATA_JOB"));
        model.addAttribute("executions", repository.findAll());
        return "index";
    }

    @PostMapping("/launchJob")
    public String launchJob(
        @RequestParam("jobName") String jobName,
        @RequestParam("files") MultipartFile[] files) throws Exception {
    	String filePath = "";
        // Enregistrement en base
        BatchExecution execution = new BatchExecution();
        execution.setJobName(jobName);
        execution.setStartTime(LocalDateTime.now());
        execution.setStatus("STARTING");
        execution = repository.save(execution);
        
        // Gestion des fichiers
        List<BatchFile> batchFiles = new ArrayList<>();
        for(MultipartFile file : files) {
            Path tempFile = Files.createTempFile("batch-", file.getOriginalFilename());
            file.transferTo(tempFile);
            
            BatchFile batchFile = new BatchFile();
            batchFile.setFileName(file.getOriginalFilename());
            batchFile.setFilePath(tempFile.toString());
            batchFile.setExecution(execution);
            batchFiles.add(batchFile);
            filePath = batchFile.getFilePath();
        }
        execution.setFiles(batchFiles);
        repository.save(execution);
        
        // Paramètres du job

	    // Build JobParameters and launch the job
	    JobParameters params = new JobParametersBuilder()
	        .addString("inputFile", filePath) 
            .addString("executionId", execution.getId().toString())
            .addLong("time", System.currentTimeMillis())
            .toJobParameters();
        
        // Exécution du job
      //  Job job = jobRegistry.getJob(jobName);
         runJob(jobName, params);
        
        return "redirect:/";
    }
    
 // Pour une exécution asynchrone :
    @Async
    public void runJobAsync(Job job, JobParameters parameters) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        jobLauncher.run(job, parameters);
    }

    // Pour la gestion des erreurs :
    @ControllerAdvice
    public class BatchExceptionHandler {
        @ExceptionHandler(JobExecutionException.class)
        public String handleError(JobExecutionException ex, Model model) {
            model.addAttribute("error", ex.getMessage());
            return "error-page";
        }
    }

    // Pour le suivi en temps réel :
    @RestController
    public class BatchStatusController {
        @GetMapping("/batch-status/{id}")
        public BatchExecution getStatus(@PathVariable Long id) {
            return repository.findById(id).orElseThrow();
        }
    }
}
