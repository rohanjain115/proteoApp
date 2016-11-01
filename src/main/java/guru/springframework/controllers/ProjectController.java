	package guru.springframework.controllers;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import guru.springframework.domain.DataFile;
import guru.springframework.domain.Experiment;
import guru.springframework.domain.Lab;
import guru.springframework.domain.Project;
import guru.springframework.domain.ProjectStatus;
import guru.springframework.domain.User;
import guru.springframework.repositories.DataFileRepository;
import guru.springframework.repositories.ExperimentRepository;
import guru.springframework.repositories.ProjectRepository;
import guru.springframework.repositories.UserRepository;
import guru.springframework.services.S3StorageService;
import guru.springframework.services.UserDetailService;

@Controller
public class ProjectController {
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	UserDetailService userDetailService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ExperimentRepository experimentRepository;
	
	@Autowired
	S3StorageService s3StorageService;
	
	@Autowired
	DataFileRepository dataFileRepository;
	
	@ModelAttribute("login_user_id")
	public long getLoginUser(){
		return userDetailService.getLoggedInUser().getUser_id();
	}
	
	 @ModelAttribute("loginuser")
	    public String loginuser(){
	    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    	return auth.getName();
	    }
	    
	 
	
	@RequestMapping(value = "/project", method = RequestMethod.GET)
	public String getDetails() {
		return "project";
	}

	@RequestMapping(value="/project/read", method = RequestMethod.POST)
	public @ResponseBody List<Project> read(@RequestParam(value="isArchive", required=false) Boolean isArchive){
		if(isArchive == null){
			isArchive = false;
		}
		String archiveStatus = ProjectStatus.Active.name();
		if(isArchive){
			archiveStatus = ProjectStatus.Archive.name();
		}
		Set<Project> sharedProjects=userDetailService.getLoggedInUser().getSharedProjects();
		Set<Project> ownedProjects = userDetailService.getLoggedInUser().getProjects();
		Set<Project> ownedProjectWithStatus = new HashSet<Project>();
		for(Project p:sharedProjects){
			User projectUser = p.getUser();
			User user = new User();
			user.setUser_id(projectUser.getUser_id());
			user.setUsername(projectUser.getUsername());
			
			Lab lab = new Lab();
			lab.setLab_id(projectUser.getLab().getLab_id());
			lab.setLabName(projectUser.getLab().getLabName());
			
			user.setLab(lab);
			p.setUser(user);
			p.setProjectOwner(p.getUser().getUser_id());
			
			p.setLoggedInUserId(userDetailService.getLoggedInUser().getUser_id());
		}
		for(Project p:ownedProjects){
			if(p.getArchiveStatus() != null && p.getArchiveStatus().equalsIgnoreCase(archiveStatus)){
				
				User projectUser = p.getUser();
				User user = new User();
				user.setUser_id(projectUser.getUser_id());
				user.setUsername(projectUser.getUsername());
				
				Lab lab = new Lab();
				lab.setLab_id(projectUser.getLab().getLab_id());
				lab.setLabName(projectUser.getLab().getLabName());
				
				user.setLab(lab);
				p.setUser(user);
				
				p.setProjectOwner(p.getUser().getUser_id());
				p.setNoOfSharedUsers(p.getUsers().size());
				ownedProjectWithStatus.add(p);
				
				p.setLoggedInUserId(userDetailService.getLoggedInUser().getUser_id());
			}
		}
		List<Project> allProjects=new ArrayList<>();
		allProjects.addAll(ownedProjectWithStatus);
		
		for(Project p : sharedProjects){
			if(p.getArchiveStatus() != null && p.getArchiveStatus().equalsIgnoreCase(archiveStatus)){
				boolean projectAdded = false;
				for(Project op : allProjects){
					if(op.getProject_id() == p.getProject_id()){
						projectAdded = true;
						break;
					}
				}
				if(!projectAdded){
					allProjects.add(p);
				}
				
			}
		}
		
		return allProjects;
	}
	
	 @RequestMapping(value = {"/project/update","/save"}, method = RequestMethod.POST)
		 public @ResponseBody Project update(@Valid @ModelAttribute Project project, BindingResult errors) {
		 	 project.setUser(userDetailService.getLoggedInUser());
		 	 if(project.getProject_id()==0){
		 		project.setArchiveStatus("Active");
		 		project.setCreatedDate(GregorianCalendar.getInstance().getTime());
		 		project.setUser(userDetailService.getLoggedInUser());
		 	 }
			 projectRepository.save(project);
			 project.setProjectOwner(userDetailService.getLoggedInUser().getUser_id());
	        return project;
	    }

	 @RequestMapping(value = "/project/delete", method = {RequestMethod.GET,RequestMethod.POST})
	    public @ResponseBody String update(@RequestParam long project_id) {
		 	Project project=projectRepository.findOne(project_id);
		 	if(project.getExperiments() != null){
		 		String experimentFolderName = project.getUser().getLab().getLabName() + S3StorageService.SUFFIX + project.getUser().getUsername() + S3StorageService.SUFFIX  + project.getProjectName() + S3StorageService.SUFFIX  + S3StorageService.EXPERIMENT_FOLDER;
		 		for(Experiment experiment : project.getExperiments()){
					 if(experiment.getDataFiles() != null){
						 for(DataFile dataFile : experiment.getDataFiles()){
							 if (dataFile != null) {
								String actualFileName = "";
								String folderName = dataFile.getFilePath();
								if (folderName.contains("/")) {
									actualFileName = folderName.substring(folderName.lastIndexOf("/") + 1);
									folderName = folderName.substring(0, folderName.lastIndexOf("/"));
								}else{
									actualFileName = dataFile.getFileName();
								}
								s3StorageService.deleteFile(actualFileName, folderName);
								dataFileRepository.delete(dataFile);
							}
						 }
					 }
					 experimentFolderName = experimentFolderName + S3StorageService.SUFFIX + experiment.getExperiment_id();
					 s3StorageService.deleteFolder(experimentFolderName);
			 	}
		 	}
		 	String projectPath = project.getUser().getLab().getLabName() + S3StorageService.SUFFIX + project.getUser().getUsername() + S3StorageService.SUFFIX  + project.getProjectName();
		 	s3StorageService.deleteFolder(projectPath);
		 	projectRepository.delete(project);
	        return "";
	    }
	 
	 @RequestMapping(value = "/shareProject", method = RequestMethod.GET)
		public String getProjectSharingDetails(@RequestParam long project_id,Model model) {
		 Project project=projectRepository.findOne(project_id);
		 Set<User> sharedUsers = project.getUsers();
		 model.addAttribute("project",project);
		 model.addAttribute("sharedUsers",sharedUsers);
		 return "shareporject";
		}
	 
	 @RequestMapping(value = "/shareProject", method = RequestMethod.POST)
		public String projectSharing(@RequestParam long project_id,@RequestParam String username, Model model) {
		 Project project=projectRepository.findOne(project_id);
		 Set<User> sharedUsers = project.getUsers();
		 User u=userRepository.findByUsername(username);
		 if(u!=null){
			 sharedUsers.add(u);
		 }
		 projectRepository.save(project);
		 model.addAttribute("project",project);
		 model.addAttribute("sharedUsers",sharedUsers);
		 return "shareporject";
		}
	 
	 @RequestMapping(value = "/revokeProject", method = RequestMethod.GET)
		public String projectRevoking(@RequestParam long project_id,@RequestParam String username, Model model) {
		 Project project=projectRepository.findOne(project_id);
		 Set<User> sharedUsers = project.getUsers();
		 User u=userRepository.findByUsername(username);
		 if(u!=null){
			 sharedUsers.remove(u);
		 }
		 projectRepository.save(project);
		 model.addAttribute("project",project);
		 model.addAttribute("sharedUsers",sharedUsers);
		 return "shareporject";
		}
	 
	 @RequestMapping(value = "/viewAddNewProject", method = RequestMethod.GET)
		public String addNewProject(Model model) {
			return "addNewProject";
		}
	@RequestMapping(value = "/addProject", method = RequestMethod.POST)
	public ModelAndView add(@Valid @ModelAttribute Project project, BindingResult errors) {
		 project.setUser(userDetailService.getLoggedInUser());
	 	 if(project.getProject_id()==0){
	 		project.setArchiveStatus("Active");
	 		project.setCreatedDate(GregorianCalendar.getInstance().getTime());
	 		project.setUser(userDetailService.getLoggedInUser());
	 	 }
		 projectRepository.save(project);
		 project.setProjectOwner(userDetailService.getLoggedInUser().getUser_id());
		 return new ModelAndView("redirect:/project");
	}
	
	@RequestMapping(value = "/updateProjectArchiveStatus", method = RequestMethod.POST)
	@ResponseBody
	public int updateArchiveStatus(@RequestParam long project_id, @RequestParam boolean isArchive){
		String archiveStatus = ProjectStatus.Active.name();
		if(isArchive){
			archiveStatus = ProjectStatus.Archive.name();
		}
		Project project = projectRepository.findOne(project_id);
		Set<Experiment> experimentList = project.getExperiments();	
		if(experimentList != null){
			for(Experiment experiment : experimentList){
				Set<DataFile> dataFileList = experiment.getDataFiles();	
				if(dataFileList != null){
					for(DataFile dataFile : dataFileList){
						String actualFileName = "";
						String folderName = dataFile.getFilePath();
						if (folderName.contains("/")) {
							actualFileName = folderName.substring(folderName.lastIndexOf("/") + 1);
							folderName = folderName.substring(0, folderName.lastIndexOf("/"));
						} else {
							actualFileName = dataFile.getFileName();
						}
						String destinationFolderName = null;
						if(isArchive && folderName.contains(S3StorageService.ACTIVE_FOLDER)){
							destinationFolderName = folderName.replace(S3StorageService.ACTIVE_FOLDER, S3StorageService.ARCHIVE_FOLDER);
						}else if(!isArchive && folderName.contains(S3StorageService.ARCHIVE_FOLDER)){
							destinationFolderName = folderName.replace(S3StorageService.ARCHIVE_FOLDER, S3StorageService.ACTIVE_FOLDER);
						}
						if(!StringUtils.isEmpty(destinationFolderName)){
							s3StorageService.createFolder(destinationFolderName);
							s3StorageService.copyFile(actualFileName, folderName, destinationFolderName);
							s3StorageService.deleteFile(actualFileName, folderName);
							dataFile.setFilePath(destinationFolderName + S3StorageService.SUFFIX + actualFileName);
							dataFileRepository.save(dataFile);
						}
						
					}
				}
			}
		}
		int value = projectRepository.setIsArchiveFor(archiveStatus, project_id);
		if(value > 0){
			experimentRepository.setIsArchiveForProject(isArchive, project);
		}
		return value;
	}
}
