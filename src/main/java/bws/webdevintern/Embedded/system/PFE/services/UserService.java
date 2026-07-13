package bws.webdevintern.Embedded.system.PFE.services;

import java.util.List;

import bws.webdevintern.Embedded.system.PFE.models.User;

public interface UserService {
	
	 User addUser(User u);
		
	 User editUser (User u);
	 
	 boolean changePassword(Long id, String currentPassword, String newPassword);
	
	 void deleteUser(Long id);
	
	 List<User> getAllUsers();
	
	 User getUserById(Long id);
	
	User getUserByEmail(String email);
	
	User findUserById(Long id);
	
    List<User> getActiveUsers();



}
