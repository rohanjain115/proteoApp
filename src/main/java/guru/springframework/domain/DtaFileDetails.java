package guru.springframework.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class DtaFileDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long file_id;
	
	private String name;
	
	@JsonFormat(pattern="yyyy-MM-dd") 
	private Date date;
	
	private String description;
	
	private long proteinIds;
	
	private long peptideIds;
	
	private double proteinFdrs;
	
	private double peptiedFdrs;
	
	private double spectrumFdrs;
	
	private String fileParameters;
	
	@Transient
	private String uploadedBy;
	
	@OneToOne(mappedBy="dtaFileDetails")
	@JoinColumn(name = "data_file_id")
	@JsonIgnore
	private DataFile dataFile;
	
	public long getFile_id() {
		return file_id;
	}

	public void setFile_id(long file_id) {
		this.file_id = file_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getProteinIds() {
		return proteinIds;
	}

	public void setProteinIds(long proteinIds) {
		this.proteinIds = proteinIds;
	}

	public long getPeptideIds() {
		return peptideIds;
	}

	public void setPeptideIds(long peptideIds) {
		this.peptideIds = peptideIds;
	}

	public double getProteinFdrs() {
		return proteinFdrs;
	}

	public void setProteinFdrs(double proteinFdrs) {
		this.proteinFdrs = proteinFdrs;
	}

	public double getPeptiedFdrs() {
		return peptiedFdrs;
	}

	public void setPeptiedFdrs(double peptiedFdrs) {
		this.peptiedFdrs = peptiedFdrs;
	}

	public double getSpectrumFdrs() {
		return spectrumFdrs;
	}

	public void setSpectrumFdrs(double spectrumFdrs) {
		this.spectrumFdrs = spectrumFdrs;
	}

	public String getFileParameters() {
		return fileParameters;
	}

	public void setFileParameters(String fileParameters) {
		this.fileParameters = fileParameters;
	}

	public DataFile getDataFile() {
		return dataFile;
	}

	public void setDataFile(DataFile dataFile) {
		this.dataFile = dataFile;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
}
