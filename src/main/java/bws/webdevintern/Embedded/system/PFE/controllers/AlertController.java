package bws.webdevintern.Embedded.system.PFE.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bws.webdevintern.Embedded.system.PFE.models.Alert;
import bws.webdevintern.Embedded.system.PFE.models.Severity;
import bws.webdevintern.Embedded.system.PFE.services.AlertService;

@RestController
@RequestMapping("/api")
public class AlertController {
	@Autowired
    private AlertService alertService;
    
    // GET ALERTS BY SENSOR
    @GetMapping("/sensors/{sensorId}/alerts")
    public List<Alert> getAlertsBySensor(@PathVariable Long sensorId){
        return alertService.getBySensor(sensorId);
    }
    
    // GET ALL ALERTS
    @GetMapping("/alerts")
    public List<Alert> getAllAlerts(){
        return alertService.getAllAlerts();
    }

    // GET UNRESOLVED ALERTS
    @GetMapping("/alerts/unresolved")
    public List<Alert> getUnresolvedAlerts(){
        return alertService.getUnresolvedAlerts();
    }

    // RESOLVE ALERT
    @PutMapping("/alerts/{id}/resolve")
    public Alert resolveAlert(@PathVariable Long id){
        return alertService.resolveAlert(id);
    }

    // COUNT UNRESOLVED ALERTS
    @GetMapping("/alerts/count/unresolved")
    public long countUnresolved(){
        return alertService.countUnresolved();
    }

    
 // COUNT BY SEVERITY
    @GetMapping("/alerts/count/severity/{severity}")
    public long countBySeverity(@PathVariable Severity severity) {
        return alertService.countBySeverity(severity);
    }

    @GetMapping("/alerts/user/{userId}")
    public ResponseEntity<List<Alert>> getAlertsByUser(@PathVariable Long userId) {
        List<Alert> alerts = alertService.getAlertsByUser(userId);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/alerts/unresolved/user/{userId}")
    public List<Alert> getUnresolvedAlertsByUser(@PathVariable Long userId) {
        return alertService.getUnresolvedAlertsByUser(userId);
    }

    @GetMapping("/alerts/user/{userId}/unresolved/count")
    public ResponseEntity<Long> countUnresolvedByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(alertService.countUnresolvedByUser(userId));
    }

}
