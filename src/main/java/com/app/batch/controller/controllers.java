package com.app.batch.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.app.batch.util.FileUploadUtil;

import jakarta.servlet.ServletContext;

@Controller
@EnableScheduling
public class controllers {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    @Autowired
    private ServletContext servletContext;

	 @Value("${location.dir.upload}")
    String uploadDir =  "/uploads"; // Path relative to webapp root
    // ... rest of the code as above
 

@PostMapping("/upload")
public String handleFileUpload(@RequestParam("file") MultipartFile file) {
	
	
	
    try {
        String uploadDir = "uploads"; // Directory to save uploaded files
        String filePath = FileUploadUtil.saveUploadedFile(file, uploadDir);
        System.out.println("File saved to: " + filePath);
        return "File uploaded successfully!";
    } catch (IOException e) {
        e.printStackTrace();
        return "File upload failed!";
    }
}
 
public String fctFileUpload( MultipartFile file) {
	
	  try {
          String uploadD = servletContext.getRealPath(uploadDir);
          if (uploadD == null) {
              // Handle the case where getRealPath returns null (rare, but possible)
              return "error"; // Or throw an exception
          }
        
          String filePath = FileUploadUtil.saveUploadedFile(file, uploadD); 
   
          System.out.println("File saved to: " + filePath + "  File uploaded successfully!");
        
        return filePath;
    } catch (IOException e) {
        e.printStackTrace();
        return "File upload failed!";
    }
}
    @GetMapping("startJob")
    //every day at 1am
    //@Scheduled(cron = "0 0 1 1/1 * ?")

    public String startBatch(@RequestParam("film_rating") String film_rating) throws Exception {

        System.out.println("batch started ............... ");

        JobParameters jobParameter = new JobParametersBuilder()
                .addDate("date", new Date())
                .addString("film_rating", film_rating)
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job, jobParameter);
        while (jobExecution.isRunning()) {
            System.out.println("......");
        }

        System.out.println(jobExecution.getStatus());

        return jobExecution.getStatus().toString();
    }
    
     

    @Autowired
    private Job importFilmsJob;

    @GetMapping("/main")
    public String showForm() {
        return "upload-form";
    }

    @PostMapping("/launchImport")
    public String launchJob(@RequestParam("file") MultipartFile file) throws Exception {
          
	    String filePath =  fctFileUpload(file);
	    
	    runJob(filePath);
        
        return "redirect:/?success=true";
    }

	public void runJob(String filePath) throws Exception {
	    File file = new File(filePath);
	    if (!file.exists()) {
	        throw new FileNotFoundException("File not found: " + filePath);
	    }else {
	        System.out.println("File path  : " + filePath );
	        
	    }
	
	    // Build JobParameters and launch the job
	    JobParameters parameters = new JobParametersBuilder()
	        .addString("inputFile", filePath)
	        .addLong("time", System.currentTimeMillis())
	        .toJobParameters();
	
	    jobLauncher.run(importFilmsJob, parameters);
	}
}
