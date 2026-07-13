package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bws.webdevintern.Embedded.system.PFE.models.User;
import bws.webdevintern.Embedded.system.PFE.repositories.UserRepository;
import bws.webdevintern.Embedded.system.PFE.services.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User addUser(User u) {
		
		if (userRepository.existsByEmail(u.getEmail())) {
			return null;
			}
			String encodedPassword = passwordEncoder.encode(u.getPassword());
			u.setPassword(encodedPassword);
			return userRepository.save(u);
	}
	

	@Override
	public User editUser(User u) {
		 User existing = userRepository.findById(u.getId()).orElseThrow();
		    existing.setFirstName(u.getFirstName());
		    existing.setLastName(u.getLastName());
		    existing.setUsername(u.getUsername());
		    existing.setEmail(u.getEmail());
		    existing.setIsActive(u.getIsActive());
		    if (u.getRoles() != null && !u.getRoles().isEmpty()) existing.setRoles(u.getRoles());
	    return userRepository.save(existing);
	}

	@Override
	public void deleteUser(Long id) {
	    userRepository.deleteById(id);
	}

	@Override
	public List<User> getAllUsers() {
	    return userRepository.findAll();
	}

	@Override
	public User getUserById(Long id) {
	    return userRepository.findById(id).orElse(null);

	}

	@Override
	public User getUserByEmail(String email) {
	    return userRepository.findUserByEmail(email);

	}

	@Override
	public User findUserById(Long id) {
	    return getUserById(id);

	}

	@Override
	public List<User> getActiveUsers() {
	    return userRepository.findByIsActiveTrue();

	}


	@Override
	public boolean changePassword(Long id, String currentPassword, String newPassword) {
	    User user = userRepository.findById(id).orElseThrow();

	    // Vérifier que l'ancien mot de passe est correct
	    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
	        return false;
	    }

	    // Encoder et sauvegarder le nouveau
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(user);
	    return true;
	}

}
