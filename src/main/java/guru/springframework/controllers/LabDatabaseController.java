package guru.springframework.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import guru.springframework.domain.Lab;
import guru.springframework.domain.LabDatabase;
import guru.springframework.domain.Organism;
import guru.springframework.domain.Source;
import guru.springframework.domain.User;
import guru.springframework.domain.YesNo;
import guru.springframework.repositories.DatabaseRepository;
import guru.springframework.repositories.LabRepository;
import guru.springframework.repositories.OrganismRepository;
import guru.springframework.repositories.SourceRepository;
import guru.springframework.repositories.UserRepository;
import guru.springframework.services.S3StorageService;
import guru.springframework.services.StorageService;
import guru.springframework.services.UserDetailService;
import guru.springframework.util.FastaReader;

@Controller
public class LabDatabaseController {

	@Autowired
	DatabaseRepository databaseRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	LabRepository labRepository;
	
	@Autowired
	OrganismRepository organismRepository;
	
	@Autowired
	SourceRepository sourceRepository;
	
	@Autowired
	FastaReader fastaReader;
	
	@Autowired
	StorageService storageService;
	
	@Autowired
	S3StorageService s3StorageService;
	
	@Autowired
	UserDetailService userDetailService;
	
	@RequestMapping(value = "/database", method = RequestMethod.GET)
	public String getDetails() {
		return "database";
	}
	
	 @ModelAttribute("loginuser")
	    public String loginuser(){
	    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    	return auth.getName();
	    }
	    
	
	
	@RequestMapping(value = "/viewAddNewDatabase", method = RequestMethod.GET)
	public String addNewDatabase(Model model) {
		model.addAttribute("sourceList", sourceRepository.findAll());
		model.addAttribute("organismList", organismRepository.findAll());
		return "addNewDatabase";
	}
	
	@RequestMapping(value="/readDatabase", method = RequestMethod.GET)
	@ResponseBody
	public List<LabDatabase> read(Principal principal) {
		String userName = principal.getName();
		User user = userRepository.findByUsername(userName);
		Lab lab = user.getLab();
		List<LabDatabase> labDatabaseList = databaseRepository.findByLab(lab);
		if(labDatabaseList != null){
			for(LabDatabase labDatabase : labDatabaseList){
				labDatabase.setReleaseDateStr(getReleaseDate(labDatabase.getReleasedDate()));
				labDatabase.setUploadDateStr(getUploadedDate(labDatabase.getUploadedDate()));
				if(userName.equals(labDatabase.getUser().getUsername())){
					labDatabase.setDeletedb(true);
				}
			
			}
		}
		return labDatabaseList;
	}

	@RequestMapping(value = "/deleteDatabase", method = RequestMethod.POST)
	@ResponseBody
	public String delete(@RequestParam long database_id) {
		LabDatabase labDatabase = databaseRepository.findOne(database_id);
		if(labDatabase != null){
			databaseRepository.delete(labDatabase);
			System.out.println("inside database delete");
		}
		
		return "";
	}
	
	@RequestMapping(value = "/addDatabase", method = RequestMethod.POST)
	public ModelAndView add(@RequestParam Map<String, String> requestMap, Principal principal) {
		String userName = principal.getName();	
		User user = userRepository.findByUsernameOrEmail(userName, userName);
		Lab lab = user.getLab();
		if(lab == null){
			// TODO add error page and redirect to it
		}
		
		LabDatabase labDatabase = new LabDatabase();
		labDatabase.setLab(lab);
		labDatabase.setUser(user);
		String addConProtein = requestMap.get("contaminant");
		String fileName = requestMap.get("upload_file_name");
		String filePath = "";
		if(addConProtein != null && addConProtein.equalsIgnoreCase(YesNo.Yes.name())){
			labDatabase.setAddConProtein(YesNo.Yes);
		}else{
			labDatabase.setAddConProtein(YesNo.No);
		}
		
		if(!StringUtils.isEmpty(fileName)){
			filePath = storageService.getDefaultFilePath().toString() + File.separator + fileName;
			labDatabase.setProteinNum(fastaReader.getproteinCount(filePath));
			if(!StringUtils.isEmpty(requestMap.get("file_size"))){
				double fileSize = Double.parseDouble(requestMap.get("file_size"));
				DecimalFormat df = new DecimalFormat("#.##");      
				fileSize = Double.valueOf(df.format(fileSize));	
				labDatabase.setSizeInKb(fileSize);
			}else{
				labDatabase.setSizeInKb(0);
			}
		}
		
		labDatabase.setDescription(requestMap.get("desc"));
		if(!StringUtils.isEmpty(filePath)){
			File file = new File(filePath);
			labDatabase.setFilePath(file.getName());
			file.delete();
		}
		
		String reverse = requestMap.get("reverse");
		if(reverse != null && reverse.equalsIgnoreCase(YesNo.Yes.name())){
			labDatabase.setGenRevSeq(YesNo.Yes);
		}else{
			labDatabase.setGenRevSeq(YesNo.No);
		}
		
		Organism organism = organismRepository.findOne(Integer.parseInt(requestMap.get("organism")));
		labDatabase.setOrganism(organism);
		
		
		Source source = sourceRepository.findOne(Integer.parseInt(requestMap.get("dbSource")));
		labDatabase.setSource(source);
		
		String year = requestMap.get("year");
		String month = requestMap.get("month");
		String date = requestMap.get("date");
		try{
			labDatabase.setReleasedDate(getDate(year, month, date));
		}catch(ParseException e){
			e.printStackTrace();
		}
		
		
		labDatabase.setUploadedBy(userName);
		year = requestMap.get("year");
		
		Calendar calendar = Calendar.getInstance();
		
		labDatabase.setUploadedDate(calendar.getTime());
		labDatabase.setVersion(requestMap.get("version"));
		
		databaseRepository.save(labDatabase);
		
		System.out.println("inside database add");
		return new ModelAndView("redirect:/database");
	}
	
	@RequestMapping(value = "/viewAddNewOrganism", method = RequestMethod.GET)
	public String addNewOrganism() {
		return "addNewOrganism";
	}
	
	@RequestMapping(value = "/viewAddNewSource", method = RequestMethod.GET)
	public String addNewSource() {
		return "addNewSource";
	}
	
	@RequestMapping(value = "/addNewSource", method = RequestMethod.POST)
	public ModelAndView addNewSource(@RequestBody String sourceName, Model model, RedirectAttributes redir){
		sourceName = sourceName.substring(sourceName.indexOf("dbSource=") + "dbSource=".length());
		Source source = sourceRepository.findBySourceName(sourceName);
		if(source != null){
			redir.addFlashAttribute("error", "Source with given name already exists");
			return new ModelAndView("redirect:/viewAddNewSource");
		}else{
			Source newSource = new Source();
			newSource.setSourceName(sourceName);
			sourceRepository.save(newSource);
			return new ModelAndView("redirect:/viewAddNewDatabase");
		}
	}
	
	@RequestMapping(value = "/addNewOrganism", method = RequestMethod.POST)
	public ModelAndView addNewOrganism(@RequestBody String organismName, Model model, RedirectAttributes redir){
		organismName = organismName.substring(organismName.indexOf("organism=") + "organism=".length());
		Organism organism = organismRepository.findByOrganismName(organismName);
		if(organism != null){
			redir.addFlashAttribute("error", "Organism with given name already exists");
			return new ModelAndView("redirect:/viewAddNewOrganism");
		}else{
			Organism newOrganism = new Organism();
			newOrganism.setOrganismName(organismName);
			organismRepository.save(newOrganism);
			return new ModelAndView("redirect:/viewAddNewDatabase");
		}
	}
	
	@RequestMapping(value = "/downloadDatabaseFile/{fileName}.{ext}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadStuff(@PathVariable String fileName, @PathVariable String ext)
	                                                                  throws IOException {
		
		InputStream inputStream = s3StorageService.download(fileName + "." + ext, "databases");
	    HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    respHeaders.setContentDispositionFormData("attachment", fileName + "." + ext);

	    InputStreamResource isr = new InputStreamResource(inputStream);
	    return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
	}	
		
	
	public Date getDate(String year, String month, String date) throws ParseException{
	
		if(month.length() == 1){
			month = "0"+month;
		}
		
		if(date.length() == 1){
			date = "0"+date;
		}
		String dateStr = year + "-"+ month+ "-"+date;
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return  formatter.parse(dateStr);
	}
	
	public String getReleaseDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		String year =  calendar.get(Calendar.YEAR) + "";
		String month = (calendar.get(Calendar.MONTH) + 1) + "";
		String dateStr = calendar.get(Calendar.DATE) + "";
		if(month.length() == 1){
			month = "0"+month;
		}
		if(dateStr.length() == 1){
			dateStr = "0"+dateStr;
		}
		return year + "-" + month + "-"+ dateStr;
	}
	
	public String getUploadedDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		String year =  calendar.get(Calendar.YEAR) + "";
		String month = (calendar.get(Calendar.MONTH) + 1) + "";
		if(month.length() == 1){
			month = "0"+month;
		}
		String dateStr = calendar.get(Calendar.DATE) + "";
		if(dateStr.length() == 1){
			dateStr = "0"+dateStr;
		}
		String hours = calendar.get(Calendar.HOUR) + "";
		String minutes = calendar.get(Calendar.MINUTE) + "";
		String seconds = calendar.get(Calendar.SECOND) + "";
		return year + "-" + month + "-"+ dateStr + " "+ hours + ":" + minutes + ":" + seconds + ".0";
	}
	
	@ModelAttribute("databaseFileFolderPath")
	public String getFolderPath() {
		User user = userDetailService.getLoggedInUser();
		if(user != null && user.getUsername() != null){
			Lab lab = user.getLab();
			if(lab != null && lab.getLabName() != null ){
				return lab.getLabName() + S3StorageService.SUFFIX + user.getUsername() + S3StorageService.SUFFIX + S3StorageService.DATABASE_FOLDER;
			}
		}
		return "databases";
	}
}
