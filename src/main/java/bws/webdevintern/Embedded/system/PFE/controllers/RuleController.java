package bws.webdevintern.Embedded.system.PFE.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bws.webdevintern.Embedded.system.PFE.exceptions.ForbiddenAccessException;
import bws.webdevintern.Embedded.system.PFE.models.Rule;
import bws.webdevintern.Embedded.system.PFE.models.User;
import bws.webdevintern.Embedded.system.PFE.repositories.UserRepository;
import bws.webdevintern.Embedded.system.PFE.services.RuleService;

@RestController
@RequestMapping("/api")

public class RuleController {
	
	@Autowired
    private RuleService ruleService;
	
	@Autowired
	private UserRepository userRepository;

	private User currentUser(Authentication authentication) {
	    return userRepository.findUserByEmail(authentication.getName());
	}

	   @PostMapping("/sensors/{sensorId}/rules")
	    public ResponseEntity<?> createRule(@RequestBody Rule rule,
	                                        @PathVariable Long sensorId) {
	        try {
	            return ResponseEntity.ok(ruleService.createRule(rule, sensorId));
	        } catch (RuntimeException e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        }
	    }
	   
		@GetMapping("/rules")
		public List<Rule> getAllRules(Authentication authentication) {
		    User user = currentUser(authentication);
		    boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
		    if (!isAdmin) {
		        throw new ForbiddenAccessException("Only admins can list all rules");
		    }
		    return ruleService.getAllRules();
		}
	   
		@GetMapping("/rules/user/{userId}")
		public List<Rule> getRulesByUser(@PathVariable Long userId, Authentication authentication) {
		    User user = currentUser(authentication);
		    boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
		    if (!isAdmin && !user.getId().equals(userId)) {
		        throw new ForbiddenAccessException("You are not allowed to view these rules");
		    }
		    return ruleService.getByUser(userId);
		}
	   
	   @GetMapping("/rules/{id}")
	   public ResponseEntity<?> getRuleById(@PathVariable Long id) {
	       try {
	           return ResponseEntity.ok(ruleService.getRuleById(id));
	       } catch (RuntimeException e) {
	           return ResponseEntity.notFound().build();
	       }
	   }

	    @GetMapping("/sensors/{sensorId}/rules")
	    public List<Rule> getRulesBySensor(@PathVariable Long sensorId, Authentication authentication){
	        return ruleService.getBySensorForUser(sensorId, currentUser(authentication));
	    }
	    
	    @PutMapping("/rules/{id}")
	    public ResponseEntity<?> updateRule(@PathVariable Long id,
	                                        @RequestBody Rule rule,
	                                        @RequestParam(required = false) Long sensorId) {
	        try {
	            return ResponseEntity.ok(ruleService.updateRule(id, rule, sensorId));
	        } catch (RuntimeException e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        }
	    }

	    @PutMapping("/rules/{id}/activate")
	    public Rule activateRule(@PathVariable Long id){
	        return ruleService.activateRule(id);
	    }

	    @PutMapping("/rules/{id}/deactivate")
	    public Rule deactivateRule(@PathVariable Long id){
	        return ruleService.deactivateRule(id);
	    }

	    @DeleteMapping("/rules/{id}")
	    public void deleteRule(@PathVariable Long id){
	        ruleService.deleteRule(id);
	    }

}
