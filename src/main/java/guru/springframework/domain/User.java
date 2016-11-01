package guru.springframework.domain;


import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {

	public User() {
	}

	public User(String firstName, String lastName, String email, String username, String password, String role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
		this.password = password;
		this.role = role;
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long user_id;
	
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String password;
	private String role;
	private String mobile;
	private String passwordHint;
	private String website;
	private String address;
	
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="lab_id")
	private Lab lab;
	
	@OneToMany(mappedBy="user")
	@JsonIgnore
	private Set<LabDatabase> labDatabases;
	
	@OneToMany(mappedBy="user")
	@JsonIgnore
	private Set<Project> projects;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
	@JsonIgnore
	public Set<Project> sharedProjects;
	
	public Lab getLab() {
		return lab;
	}

	public void setLab(Lab lab) {
		this.lab = lab;
	}
	
	
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return String.format("Person[id=%d, firstName='%s', lastName='%s']", user_id, firstName, lastName);
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public Set<LabDatabase> getLabDatabases() {
		return labDatabases;
	}

	public void setLabDatabases(Set<LabDatabase> labDatabases) {
		this.labDatabases = labDatabases;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPasswordHint() {
		return passwordHint;
	}

	public void setPasswordHint(String passwordHint) {
		this.passwordHint = passwordHint;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	public Set<Project> getSharedProjects() {
		return sharedProjects;
	}

	public void setSharedProjects(Set<Project> sharedProjects) {
		this.sharedProjects = sharedProjects;
	}

	

}
