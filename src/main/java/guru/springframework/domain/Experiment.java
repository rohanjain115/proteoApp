package guru.springframework.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import guru.springframework.services.S3StorageService;

@Entity
public class Experiment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long experiment_id;
	
	private String sampleName;
	private String sampleDescription;
	@JsonFormat(pattern="yyyy-MM-dd") 
	private Date experimentDate;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.S") 
	private Date createDate;
	private String description;
	
	private boolean isArchive;
	
	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;
	
	@OneToMany(mappedBy="experiment", cascade = CascadeType.ALL)
	private Set<DataFile> dataFiles;
	
	@OneToOne	
	@JoinColumn(name="INSTRUMENT_ID", nullable=true, insertable=true, updatable=true)
	private Instrument instrument;
	
	@Transient
	private String folderPath;

	public long getExperiment_id() {
		return experiment_id;
	}

	public void setExperiment_id(long experiment_id) {
		this.experiment_id = experiment_id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Set<DataFile> getDataFiles() {
		return dataFiles;
	}

	public void setDataFiles(Set<DataFile> dataFiles) {
		this.dataFiles = dataFiles;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getSampleDescription() {
		return sampleDescription;
	}

	public void setSampleDescription(String sampleDescription) {
		this.sampleDescription = sampleDescription;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getExperimentDate() {
		return experimentDate;
	}

	public void setExperimentDate(Date experimentdate) {
		this.experimentDate = experimentdate;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public boolean isArchive() {
		return isArchive;
	}

	public void setArchive(boolean isArchive) {
		this.isArchive = isArchive;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public String getFolderPath() {
		if(project != null && project.getUser() != null && project.getUser().getLab() !=null){
			if(!StringUtils.isEmpty(project.getProjectName()) && !StringUtils.isEmpty(project.getUser().getUsername()) && !StringUtils.isEmpty(project.getUser().getLab().getLabName())){
				return project.getUser().getLab().getLabName() + S3StorageService.SUFFIX + project.getUser().getUsername() + S3StorageService.SUFFIX  + project.getProjectName() + S3StorageService.SUFFIX  + S3StorageService.EXPERIMENT_FOLDER + S3StorageService.SUFFIX + String.valueOf(experiment_id);
			}
		}
		return "experiments";
	}
}
