package guru.springframework.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import guru.springframework.services.UserDetailService;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	UserDetailService userService;
	
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
        		.antMatchers("/login","/console/**","/register","/lab-register").permitAll()
        		.antMatchers("/message","/lab","/user").hasRole("ADMIN")
        		.anyRequest().authenticated()
        		.and().formLogin().loginPage("/login").defaultSuccessUrl("/home",true).usernameParameter("username").passwordParameter("password").successForwardUrl("/home")
    	        .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout")
    	        .and().authorizeRequests().anyRequest().authenticated()
                ;

        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }
    
    @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userService);
	}
}