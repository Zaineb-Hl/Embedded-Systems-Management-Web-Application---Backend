package bws.webdevintern.Embedded.system.PFE.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bws.webdevintern.Embedded.system.PFE.models.SensorType;
import bws.webdevintern.Embedded.system.PFE.services.SensorTypeService;

@RestController
@RequestMapping("/api/sensor-types")
public class SensorTypeController {
	
	@Autowired
	private SensorTypeService sensorTypeService;

	 	@PostMapping
	    public SensorType createSensorType(@RequestBody SensorType type){
	        return sensorTypeService.create(type);
	    }

	    @GetMapping
	    public List<SensorType> getAllTypes(){
	        return sensorTypeService.getAll();
	    }

	    // GET BY ID
	    @GetMapping("/{id}")
	    public SensorType getTypeById(@PathVariable Long id) {
	        return sensorTypeService.getSensorTypeById(id);
	    }

	    
	    @PutMapping("/{id}")
	    public SensorType updateType(@PathVariable Long id, @RequestBody SensorType type) {
	        return sensorTypeService.updateSensorType(id, type);
	    }
	    
	    @DeleteMapping("/{id}")
	    public ResponseEntity<?> deleteType(@PathVariable Long id) {
	        try {
	            String message = sensorTypeService.delete(id);
	            return ResponseEntity.ok(message);
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	        }
	    }

}
