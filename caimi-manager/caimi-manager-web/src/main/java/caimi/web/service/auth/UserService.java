package caimi.web.service.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import caimi.web.beans.User;

@Service
@Transactional
public class UserService implements UserDetailsService{
	
	/*
	 * spring boot security
	 */
	public int register(User user) {
		
		return 1;
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return new User();
	}

}
