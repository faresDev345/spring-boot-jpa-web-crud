package com.app.batchapp.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.batchapp.model.JobStatus;

public interface JobBatchService <JobBatch>{
	List<JobBatch> getAllJobBatchs();
	
	JobBatch saveJobBatch(JobBatch jobBatch);
	
	JobBatch getJobBatchById(Long id);
	
	JobBatch updateJobBatch(JobBatch jobBatch);
    void updateAvailableStatus(Long id, boolean  status) ;
    
    void updateStatus(Long id, JobStatus  status) ;
    
    
	void deleteJobBatchById(Long id);

	Page<JobBatch> findAll(Pageable paging);

	Page<JobBatch>  findByNameContainingIgnoreCasePage(String keyword, Pageable paging);
	
}
