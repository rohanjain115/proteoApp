package guru.springframework.controllers;

import java.util.List;

import javax.mail.internet.AddressException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import guru.springframework.domain.Lab;
import guru.springframework.repositories.LabRepository;
import guru.springframework.repositories.SiteUpdateMessageRepository;
import guru.springframework.services.Utils;

@Controller
@RequestMapping("/lab")
public class LabController {

	@Autowired
	LabRepository labRepository;
	
	@Autowired
	SiteUpdateMessageRepository messageRepository;
	
	@Autowired
	Utils utils;
	
	@RequestMapping({"/",""})
	public String get(){
		return "admin-lab";
	}
	
	 @ModelAttribute("loginuser")
	    public String loginuser(){
	    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    	return auth.getName();
	    }
	    
	 
	@RequestMapping(value="/read", method = RequestMethod.POST)
	public @ResponseBody List<Lab> read(){
		return labRepository.findAll();
	}
	 @RequestMapping(value = "/update", method = RequestMethod.POST)
		 public @ResponseBody Lab update(@Valid @ModelAttribute Lab lab, BindingResult errors) {
		 	if(lab.isApproved()){
		 		try {
					utils.sendEmail(lab.getRequestorEmail(), "Lab add request approved", "Your Lab add request approved. Kindly Register yourself!");
				} catch (AddressException e) {
					e.printStackTrace();
				}
		 	}
			 labRepository.save(lab);
	        return lab;
	    }

	 @RequestMapping(value = "/delete", method = RequestMethod.POST)
	    public @ResponseBody String update(@RequestParam long lab_id) {
		 Lab msg=labRepository.findOne(lab_id);
		 	labRepository.delete(msg);
	        return "";
	    }
}
