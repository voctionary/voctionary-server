package vsb.fei.voctionary.service.db;

import org.springframework.security.core.userdetails.UserDetailsService;

import vsb.fei.voctionary.model.User;

public interface UserService extends UserDetailsService {
		
	public User findByEmail(String email);
			
	public User createUser(User user);
	
	public User updateUser(User user);
		
	void deleteAll();
	
}
