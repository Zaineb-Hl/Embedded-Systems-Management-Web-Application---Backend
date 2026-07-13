package bws.webdevintern.Embedded.system.PFE.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bws.webdevintern.Embedded.system.PFE.DTO.LoginRequest;
import bws.webdevintern.Embedded.system.PFE.DTO.SignupRequest;
import bws.webdevintern.Embedded.system.PFE.models.Role;
import bws.webdevintern.Embedded.system.PFE.models.User;
import bws.webdevintern.Embedded.system.PFE.security.JwtUtil;
import bws.webdevintern.Embedded.system.PFE.services.UserService;
import bws.webdevintern.Embedded.system.PFE.servicesImpl.UserServiceImpl;
import bws.webdevintern.Embedded.system.PFE.repositories.RoleRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
    private UserService userService;
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	UserDetailsService userDetailsService;
	@Autowired
	AuthenticationManager authenticationManager;
	
	private final JwtUtil jwtUtil = new JwtUtil();

	@PostMapping("/signup")
	public ResponseEntity<?> addUser(@RequestBody SignupRequest signUpRequest) {

		User user = new User();
		user.setFirstName(signUpRequest.getFirstName());
		user.setLastName(signUpRequest.getLastName());
		user.setEmail(signUpRequest.getEmail());
		user.setUsername(signUpRequest.getUsername());  
		user.setPassword(signUpRequest.getPassword());
		List<Role> roles = new ArrayList<>();
		for (String roleName : signUpRequest.getRoles() ) {
			Role role = roleRepository.findByName(roleName)
					.orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
			roles.add(role);
		}

		user.setRoles(roles);

		User createdUser = userService.addUser(user);

		if (createdUser == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email is used"));
		}

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("message", "User created with success", "user", createdUser));
	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest) {
		Map<String, Object> map = new HashMap<>();

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			if (authentication.isAuthenticated()) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
				User user = userServiceImpl.getUserByEmail(loginRequest.getEmail());
				String token = jwtUtil.createToken(userDetails, user);
				map.put("status", HttpStatus.OK.value());
				map.put("message", "Authentication successful");
				map.put("token", token);
				return ResponseEntity.ok(map);
			} else {
				map.put("status", HttpStatus.UNAUTHORIZED.value());
				map.put("message", "Authentication failed");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
			}
		} catch (BadCredentialsException ex) {
			map.put("status", HttpStatus.UNAUTHORIZED.value());
			map.put("message", "Bad credentials");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
		} catch (LockedException ex) {
			map.put("status", HttpStatus.UNAUTHORIZED.value());
			map.put("message", "Your account is locked");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
		} catch (DisabledException ex) {
			map.put("status", HttpStatus.UNAUTHORIZED.value());
			map.put("message", "Your account is disabled");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
		} catch (AuthenticationException ex) {
			map.put("status", HttpStatus.UNAUTHORIZED.value());
			map.put("message", "Authentication failed");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
		}
	}

	// CREATE USER
	@PostMapping
	public User createUser(@RequestBody User user) {
	    return userService.addUser(user);
	}

	// GET USER BY ID
	@GetMapping("/{id}")
	public User getUserById(@PathVariable Long id){
	    return userService.getUserById(id);
	}


	// GET ALL USERS
	@GetMapping
	public List<User> getAllUsers(){
	    return userService.getAllUsers();
	}

	// GET ACTIVE USERS
	@GetMapping("/active")
	public List<User> getActiveUsers(){
	    return userService.getActiveUsers();
	}

	// UPDATE USER
	@PutMapping("/{id}")
	public ResponseEntity<?> editUser(@PathVariable Long id, @RequestBody User user) {
	    user.setId(id);
	    User updated = userService.editUser(user);
	    return ResponseEntity.ok(updated);
	}

	// DELETE USER
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id){
	    userService.deleteUser(id);
	}


	// CHANGE PASSWORD 
	@PutMapping("/{id}/change-password")
	public ResponseEntity<?> changePassword(
	        @PathVariable Long id,
	        @RequestBody Map<String, String> request) {

	    String currentPassword = request.get("currentPassword");
	    String newPassword     = request.get("newPassword");

	    boolean success = userService.changePassword(id, currentPassword, newPassword);

	    if (!success) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("message", "Current password is incorrect"));
	    }

	    return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
	}
}
