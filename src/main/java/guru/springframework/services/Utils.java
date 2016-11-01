package guru.springframework.services;

import java.nio.charset.Charset;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import it.ozimov.springboot.templating.mail.model.Email;
import it.ozimov.springboot.templating.mail.model.impl.EmailImpl;
import it.ozimov.springboot.templating.mail.service.EmailService;

@Service
public class Utils {

	@Autowired
	public EmailService emailService;

	public void sendEmail(String to,String subject,String text) throws AddressException {
	   final Email email = EmailImpl.builder()
	        .from(new InternetAddress(to))
	        .replyTo(new InternetAddress(to))
	        .to(Lists.newArrayList(new InternetAddress(to)))
	        .subject(subject)
	        .body(text)
	        .encoding(Charset.forName("UTF-8")).build();
	
	   emailService.send(email);
	}
	
}
