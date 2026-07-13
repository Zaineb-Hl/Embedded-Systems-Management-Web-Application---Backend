package bws.webdevintern.Embedded.system.PFE.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
import bws.webdevintern.Embedded.system.PFE.services.SensorCacheService;
import bws.webdevintern.Embedded.system.PFE.services.SensorInputService;

@RestController
@RequestMapping("/api")
public class SensorInputController {
	
	@Autowired
    private SensorInputService sensorInputService;
	
	 @Autowired  
	 private SensorCacheService sensorCacheService;
	
	 // SAVE SENSOR INPUT
    @PostMapping("/sensors/{sensorId}/inputs")
    public SensorInput saveInput(@PathVariable Long sensorId,
                                 @RequestBody SensorInput input){
        return sensorInputService.saveInput(sensorId, input);
    }

    // GET ALL INPUTS BY SENSOR
    @GetMapping("/sensors/{sensorId}/inputs")
    public List<SensorInput> getInputsBySensor(@PathVariable Long sensorId){
        return sensorInputService.getBySensor(sensorId);
    }

    // GET LATEST VALUES
    @GetMapping("/sensors/{sensorId}/inputs/latest")
    public List<SensorInput> getLatestValues(@PathVariable Long sensorId){
        return sensorInputService.getLatestValues(sensorId);
    }

    // GET INPUTS BETWEEN DATES
    @GetMapping("/sensors/{sensorId}/inputs/between")
    public List<SensorInput> getBetweenDates(@PathVariable Long sensorId,
                                             @RequestParam LocalDateTime start,
                                             @RequestParam LocalDateTime end){
        return sensorInputService.getBetweenDates(sensorId, start, end);
    }
    
    // lecture directe depuis Hazelcast (ultra-rapide) ───────────
    // Utilisé par Angular pour afficher la valeur "live" du capteur sur le dashboard

    @GetMapping("/sensors/{sensorId}/inputs/current")
    public ResponseEntity<SensorInput> getCurrentValue(@PathVariable Long sensorId) {
        SensorInput cached = sensorCacheService.getLatestFromCache(sensorId);
        if (cached == null) {
            // Capteur hors ligne
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cached);
    }


	
}
