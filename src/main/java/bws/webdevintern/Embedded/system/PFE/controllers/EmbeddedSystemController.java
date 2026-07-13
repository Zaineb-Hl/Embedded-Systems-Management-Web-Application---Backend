package bws.webdevintern.Embedded.system.PFE.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import bws.webdevintern.Embedded.system.PFE.models.User;
import bws.webdevintern.Embedded.system.PFE.repositories.UserRepository;

import bws.webdevintern.Embedded.system.PFE.models.EmbeddedSystem;
import bws.webdevintern.Embedded.system.PFE.services.EmbeddedSystemService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/systems")
public class EmbeddedSystemController {

	@Autowired
    private EmbeddedSystemService systemService;
	@Autowired
	private UserRepository userRepository;
	
	 @PostMapping("/user/{userId}")
	    public EmbeddedSystem createSystem(@RequestBody EmbeddedSystem system,
	                                       @PathVariable Long userId){
	        return systemService.createSystem(system,userId);
	    }
	 
	 @GetMapping("/{id}")
	 public EmbeddedSystem getSystem(@PathVariable Long id, Authentication authentication){
	     return systemService.getEmbeddedSystemByIdForUser(id, currentUser(authentication));
	 }
	 
	 @GetMapping
	    public List<EmbeddedSystem> getAllSystems(){
	        return systemService.getAllEmbeddedSystems();
	    }
	 
	 @GetMapping("/user/{userId}")
	 public List<EmbeddedSystem> getSystemsByUser(@PathVariable Long userId, Authentication authentication) {
	     return systemService.getByOwnerForUser(userId, currentUser(authentication));
	 }

	 @PutMapping("/{id}")
	 public EmbeddedSystem updateSystem(@PathVariable Long id,
	                                    @RequestBody EmbeddedSystem system,
	                                    Authentication authentication){
	     return systemService.updateSystem(id, system, currentUser(authentication));
	 }

	 @DeleteMapping("/{id}")
	 public void deleteSystem(@PathVariable Long id, Authentication authentication){
	     systemService.deleteEmbeddedSystem(id, currentUser(authentication));
	 }
	 
	    private User currentUser(Authentication authentication) {
	        return userRepository.findUserByEmail(authentication.getName());
	    }   
	    

}
