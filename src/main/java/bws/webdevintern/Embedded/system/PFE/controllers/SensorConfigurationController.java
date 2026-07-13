package bws.webdevintern.Embedded.system.PFE.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bws.webdevintern.Embedded.system.PFE.models.SensorConfiguration;
import bws.webdevintern.Embedded.system.PFE.services.SensorConfigurationService;

@RestController
@RequestMapping("/api/sensor-configurations")
public class SensorConfigurationController {
	
	@Autowired
    private SensorConfigurationService configurationService;

	// CREATE CONFIGURATION
    @PostMapping("/sensor/{sensorId}")
    public SensorConfiguration createConfiguration(@PathVariable Long sensorId,
                                                    @RequestBody SensorConfiguration config){
        return configurationService.createConfiguration(sensorId, config);
    }

 // UPDATE CONFIGURATION
    @PutMapping("/{id}")
    public SensorConfiguration editSensorConfiguration(@PathVariable Long id,
                                                        @RequestBody SensorConfiguration config,
    													@RequestParam(required = false) Long sensorId){
        return configurationService.editSensorConfiguration(id, config, sensorId);
    }

    // ACTIVATE CONFIGURATION
    @PutMapping("/{id}/activate")
    public SensorConfiguration activateConfiguration(@PathVariable Long id){
        return configurationService.activateConfiguration(id);
    }
    
    // DEACTIVATE CONFIGURATION
    @PutMapping("/{id}/deactivate")
    public SensorConfiguration deactivateConfiguration(@PathVariable Long id) {
        return configurationService.deactivateConfiguration(id);
    }
    
    //GET ALL CONFIGURATIONS
    @GetMapping
    public List<SensorConfiguration> getAllSensorConfig() {
        return configurationService.getAllSensorConfig();
    }

    // GET CONFIGURATIONS BY SENSOR
    @GetMapping("/sensor/{sensorId}")
    public List<SensorConfiguration> getConfigurationsBySensor(@PathVariable Long sensorId){
        return configurationService.getBySensor(sensorId);
    }
    
    //Get CONFIGURATIONS BY id
    @GetMapping("/{id}")
    public SensorConfiguration getConfigurationsById(@PathVariable Long id){
        return configurationService.getSensorConfigById(id);
    }
    

    // DELETE CONFIGURATION
    @DeleteMapping("/{id}")
    public String deleteConfiguration(@PathVariable Long id){
        return configurationService.deleteConfiguration(id);
    }

}
