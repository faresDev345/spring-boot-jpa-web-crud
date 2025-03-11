package com.app.batchapp.dto;

import java.util.ArrayList;
import java.util.List;

import com.app.batchapp.model.JobStatus;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; 

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobBatchForm {
 
	private long id; 
	private String code;  
	private String name;  
	private long nbrFiles ;
	private boolean available;
	private long nbrAvailable ;
	private String description;
	
 

    private String filePath; 
    
    @Enumerated(EnumType.STRING)
    private JobStatus status; // "PENDING", "PROCESSING", "SUCCESS", "FAILED"

    private String resultMessage;
	 
    private List<Parameter> parameters = new ArrayList<>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getNbrFiles() {
		return nbrFiles;
	}

	public void setNbrFiles(long nbrFiles) {
		this.nbrFiles = nbrFiles;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public long getNbrAvailable() {
		return nbrAvailable;
	}

	public void setNbrAvailable(long nbrAvailable) {
		this.nbrAvailable = nbrAvailable;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	} 
	
}

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Parameter {
	private String key;
	private String value;

}