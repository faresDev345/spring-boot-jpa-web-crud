package com.app.batchapp.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity

@Table(name = "APP_REPO_JOBATCH")
@Getter
@Setter
public class JobBatch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private long id;

	@Column
	@NonNull
	private String code;

	@Column
	@NonNull
	private String name;

	@Column
	private long nbrFiles = 0;

	@Column
	private boolean available = true;

	@Column
	private long nbrAvailable = 0;

	@Column(columnDefinition = "TEXT")
	private String description;

	@ElementCollection
	@CollectionTable(name = "user_form_data_params", joinColumns = @JoinColumn(name = "user_form_data_id"))
	private List<Parameter> parameters = new ArrayList<>();

	private String filePath;

	@Enumerated(EnumType.STRING)
	private JobStatus status; // "PENDING", "PROCESSING", "SUCCESS", "FAILED"

	private String resultMessage;

	public JobBatch() {
		super();
	}

	public JobBatch(String code, String name, long nbrAvailable, boolean available) {
		super();
		this.name = name;
		this.code = code;
		this.nbrAvailable = nbrAvailable;
		this.available = available;
	}

	public JobBatch(String code, String name, long nbrFiles, long nbrAvailable, boolean available) {
		super();
		this.name = name;
		this.code = code;
		this.nbrAvailable = nbrAvailable;

		this.nbrFiles = nbrFiles;
		this.available = available;
	}

	public JobBatch(String code, String name, String description, long nbrFiles, long nbrAvailable, boolean available) {
		super();
		this.description = description;
		this.name = name;
		this.code = code;

		this.nbrFiles = nbrFiles;
		this.nbrAvailable = nbrAvailable;
		this.available = available;

	}

	public void addParameter(String key, String value) {
		parameters.add(new Parameter(key, value));
	}

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

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
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

	
	
}

@Embeddable
@Getter
@Setter
@NoArgsConstructor
class Parameter {
	public Parameter(String key2, String value2) {
		key=key2;
		value=value2;
	}
	@Column(name = "key_param")
	private String key;
	@Column(name = "value_param")
	private String value;

}
