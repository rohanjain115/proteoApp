package guru.springframework.controllers;

import java.util.GregorianCalendar;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingErrorProcessor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import guru.springframework.domain.SiteUpdateMessage;
import guru.springframework.repositories.SiteUpdateMessageRepository;
import guru.springframework.repositories.UserRepository;

@Controller
@RequestMapping("/message")
public class MessageController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SiteUpdateMessageRepository messageRepository;
	
	@RequestMapping({"/",""})
	public String get(){
		return "admin-message";
	}
	
	 @ModelAttribute("loginuser")
	    public String loginuser(){
	    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    	return auth.getName();
	    }
	    
	 
	 
	@RequestMapping(value="/read", method = RequestMethod.POST)
	public @ResponseBody List<SiteUpdateMessage> read(){
		return messageRepository.findAll();
	}
	 @RequestMapping(value = "/update", method = RequestMethod.POST)
	    //public @ResponseBody SiteUpdateMessage update(@RequestParam String updateText) {
		 public @ResponseBody SiteUpdateMessage update(@Valid @ModelAttribute SiteUpdateMessage msg, BindingResult errors) {
		 	msg.setUpdateDate(GregorianCalendar.getInstance().getTime());
			 messageRepository.save(msg);
	        return msg;
	    }

	 @RequestMapping(value = "/delete", method = RequestMethod.POST)
	    public @ResponseBody String update(@RequestParam long updateId) {
		 SiteUpdateMessage msg=messageRepository.findOne(updateId);
		 	messageRepository.delete(msg);
	        return "";
	    }
}
