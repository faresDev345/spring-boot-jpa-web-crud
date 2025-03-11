package com.app.batchapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.batchapp.model.JobBatch;
import com.app.batchapp.model.JobStatus;

import jakarta.transaction.Transactional;


@Repository
@Transactional
public interface JobBatchRepository extends JpaRepository<JobBatch, Long>{
	List<JobBatch> findByName(String name);

	Page<JobBatch> findByNameContainingIgnoreCase(String keyword, Pageable paging);
	 @Query("UPDATE JobBatch t set t.available = :status WHERE t.id = :id")
	  @Modifying
	public void updateAvailableStatus(Long id, Boolean status) ;
	 
	 @Query("UPDATE JobBatch t set t.status = :status WHERE t.id = :id")
	  @Modifying
	public void updateStatus(Long id, JobStatus status) ;


}
