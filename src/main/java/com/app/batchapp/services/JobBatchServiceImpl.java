package com.app.batchapp.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.batchapp.dto.JobBatchForm;
import com.app.batchapp.model.JobBatch;
import com.app.batchapp.model.JobStatus;
import com.app.batchapp.repository.JobBatchRepository;

import jakarta.persistence.EntityNotFoundException;
@Service
public class JobBatchServiceImpl implements JobBatchService<JobBatch> {

	  private final JobBatchRepository jobBatchRepository;

	public JobBatchServiceImpl(JobBatchRepository jobBatchRepository) {
		super();
		this.jobBatchRepository = jobBatchRepository;
	}

	@Override
	public List<JobBatch> getAllJobBatchs() {
		return jobBatchRepository.findAll();
	}

	@Override
	public JobBatch saveJobBatch(JobBatch jobBatch) {
		return jobBatchRepository.save(jobBatch);
	}

	@Override
	public JobBatch getJobBatchById(Long id) {
		boolean hasExist = jobBatchRepository.existsById(id);
		if (!hasExist)
			new EntityNotFoundException(
					new Exception("Exception : JobBatch not founded by id : " + id));
		return jobBatchRepository.findById(id).get();
	}

	@Override
	public JobBatch updateJobBatch(JobBatch jobBatch) {
		return jobBatchRepository.save(jobBatch);
	}

	@Override
	public void deleteJobBatchById(Long id) {

		// check product exists by id
		boolean hasExist = jobBatchRepository.existsById(id);
		System.out.println(hasExist);
		if (!hasExist)
			new EntityNotFoundException(
					new Exception("Exception while performing action delete : JobBatch not founded by id : " + id));
		else
			jobBatchRepository.deleteById(id);

	}
	
	public Page<JobBatch> findAll(Pageable paging) {
		return jobBatchRepository.findAll(paging);
	}

	public Page<JobBatch> findByNameContainingIgnoreCasePage(String keyword, Pageable paging) {
		return jobBatchRepository.findByNameContainingIgnoreCase(keyword, paging);
	}

	@Override
	public void updateAvailableStatus(Long id, boolean available) {
		jobBatchRepository.updateAvailableStatus(id, available);
	}

	@Override
	public void updateStatus(Long id, JobStatus status) {
		jobBatchRepository.updateStatus(id, status);
		
	}
	
	
}
