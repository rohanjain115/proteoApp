package guru.springframework.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long project_id;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@OneToMany(mappedBy="project", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Experiment> experiments;
	
	@ManyToMany(cascade=CascadeType.ALL)  
	@JoinTable(name="user_project", joinColumns=@JoinColumn(name="project_id"), inverseJoinColumns=@JoinColumn(name="user_id"))
	@JsonIgnore
	private Set<User> users;
	
	@Transient
	private long projectOwner;
	 
	private String projectName;
	
	@JsonFormat(pattern="yyyy-MM-dd") 
	private Date createdDate;
	
	private String description;
	
	private String archiveStatus;
	
	private boolean canDelete;
	
	private int noOfSharedUsers;
	
	@Transient
	private long loggedInUserId;
	 
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Experiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(Set<Experiment> experiments) {
		this.experiments = experiments;
	}

	public long getProject_id() {
		return project_id;
	}

	public void setProject_id(long project_id) {
		this.project_id = project_id;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.noOfSharedUsers = users.size();
		this.users = users;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public int getNoOfSharedUsers() {
		return noOfSharedUsers;
	}

	public void setNoOfSharedUsers(int noOfSharedUsers) {
		this.noOfSharedUsers = noOfSharedUsers;
	}

	public long getProjectOwner() {
		return projectOwner;
	}

	public void setProjectOwner(long projectOwner) {
		this.projectOwner = projectOwner;
	}

	public String getArchiveStatus() {
		return archiveStatus;
	}

	public void setArchiveStatus(String archiveStatus) {
		this.archiveStatus = archiveStatus;
	}
	
	@Override
	public boolean equals(Object obj) {
		return project_id == ((Project)obj).getProject_id();
	}
	@Override
	public int hashCode() {
		Long value  = new Long(project_id);
		return value.hashCode();
	}

	public long getLoggedInUserId() {
		return loggedInUserId;
	}

	public void setLoggedInUserId(long loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}
}
