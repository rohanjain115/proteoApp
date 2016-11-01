package guru.springframework.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Lab {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long lab_id;
	
	private String labUrl;
	private String labName;
	private String requestorEmail;
	private String labHeadFirstName;
	private String labHeadLastName;
	private String labHeadEmail;
	private boolean approved; 
	
	@OneToMany(mappedBy="lab")
	@JsonIgnore
	private Set<User> users;
	
	@OneToMany(mappedBy="lab")
	@JsonIgnore
	private Set<LabDatabase> labDatabases;
	
	
	public long getLab_id() {
		return lab_id;
	}

	public void setLab_id(long lab_id) {
		this.lab_id = lab_id;
	}

	public Set<LabDatabase> getLabDatabases() {
		return labDatabases;
	}

	public void setLabDatabases(Set<LabDatabase> labDatabases) {
		this.labDatabases = labDatabases;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getLabUrl() {
		return labUrl;
	}

	public void setLabUrl(String labUrl) {
		this.labUrl = labUrl;
	}

	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	public String getRequestorEmail() {
		return requestorEmail;
	}

	public void setRequestorEmail(String requestorEmail) {
		this.requestorEmail = requestorEmail;
	}

	public String getLabHeadFirstName() {
		return labHeadFirstName;
	}

	public void setLabHeadFirstName(String labHeadFirstName) {
		this.labHeadFirstName = labHeadFirstName;
	}

	public String getLabHeadLastName() {
		return labHeadLastName;
	}

	public void setLabHeadLastName(String labHeadLastName) {
		this.labHeadLastName = labHeadLastName;
	}

	public String getLabHeadEmail() {
		return labHeadEmail;
	}

	public void setLabHeadEmail(String labHeadEmail) {
		this.labHeadEmail = labHeadEmail;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	
}
