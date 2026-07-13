package bws.webdevintern.Embedded.system.PFE.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
import bws.webdevintern.Embedded.system.PFE.models.Sensor;
import bws.webdevintern.Embedded.system.PFE.models.User;
import bws.webdevintern.Embedded.system.PFE.repositories.UserRepository;
import bws.webdevintern.Embedded.system.PFE.services.SensorService;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
	
	@Autowired
    private SensorService sensorService;
	
	@Autowired
	private UserRepository userRepository;

	private User currentUser(Authentication authentication) {
	    return userRepository.findUserByEmail(authentication.getName());
	}


	 @PostMapping("/system/{systemId}/type/{typeId}")
	    public Sensor createSensor(@RequestBody Sensor sensor,
	                               @PathVariable Long systemId,
	                               @PathVariable Long typeId){
	        return sensorService.createSensor(sensor, systemId, typeId);
	    }
	 
	 @GetMapping
	 public List<Sensor> getAllSensors() {
	     return sensorService.getAllSensors();
	 }

    @GetMapping("/{id}")
    public Sensor getSensor(@PathVariable Long id, Authentication authentication){
        return sensorService.getByIdForUser(id, currentUser(authentication));
    }

    // GET SENSORS BY SYSTEM
    @GetMapping("/system/{systemId}")
    public List<Sensor> getSensorsBySystem(@PathVariable Long systemId){
        return sensorService.getBySystem(systemId);
    }

 // UPDATE SENSOR
    @PutMapping("/{id}")
    public Sensor updateSensor(@PathVariable Long id,
            @RequestBody Sensor sensor,
            @RequestParam(required = false) Long systemId,
            @RequestParam(required = false) Long typeId){
        return sensorService.updateSensor(id, sensor, systemId, typeId);
    }

    // ACTIVATE SENSOR
    @PutMapping("/{id}/activate")
    public Sensor activateSensor(@PathVariable Long id){
        return sensorService.activateSensor(id);
    }

    // DEACTIVATE SENSOR
    @PutMapping("/{id}/deactivate")
    public Sensor deactivateSensor(@PathVariable Long id){
        return sensorService.deactivateSensor(id);
    }

    // DELETE SENSOR
    @DeleteMapping("/{id}")
    public void deleteSensor(@PathVariable Long id){
        sensorService.deleteSensor(id);
    }
    
    // GET SENSORS BY USER
    @GetMapping("/user/{userId}")
    public List<Sensor> getSensorsByUser(@PathVariable Long userId, Authentication authentication){
        User user = currentUser(authentication);
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
        if (!isAdmin && !user.getId().equals(userId)) {
            throw new ForbiddenAccessException("You are not allowed to view these sensors");
        }
        return sensorService.getByUser(userId);
    }

}
