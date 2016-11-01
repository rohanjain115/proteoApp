package guru.springframework.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class DataFile {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long data_file_id;
	
	@ManyToOne
	@JoinColumn(name="experiment_id")
	@JsonIgnore
	private Experiment experiment;
	
	private String fileName;
	
	private String filePath;
	
	private double fileSize;
	
	@Transient
	private boolean dtaFile;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JsonIgnore
	private DtaFileDetails dtaFileDetails;
	
	private String uploadedBy;
	
	public long getData_file_id() {
		return data_file_id;
	}

	public void setData_file_id(long data_file_id) {
		this.data_file_id = data_file_id;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public DtaFileDetails getDtaFileDetails() {
		return dtaFileDetails;
	}

	public void setDtaFileDetails(DtaFileDetails dtaFileDetails) {
		this.dtaFileDetails = dtaFileDetails;
	}

	public double getFileSize() {
		return fileSize;
	}

	public void setFileSize(double fileSize) {
		this.fileSize = fileSize;
	}

	public boolean isDtaFile() {
		return dtaFile;
	}

	public void setDtaFile(boolean dtaFile) {
		this.dtaFile = dtaFile;
	}
	
	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
}
