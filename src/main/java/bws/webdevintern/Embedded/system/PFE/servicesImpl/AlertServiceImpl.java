package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bws.webdevintern.Embedded.system.PFE.models.Alert;
import bws.webdevintern.Embedded.system.PFE.models.Rule;
import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
import bws.webdevintern.Embedded.system.PFE.models.Severity;
import bws.webdevintern.Embedded.system.PFE.repositories.AlertRepository;
import bws.webdevintern.Embedded.system.PFE.services.AlertService;

@Service
public class AlertServiceImpl implements AlertService{

	@Autowired
    private AlertRepository alertRepository;

	@Override
	public Alert createAlert(Rule rule, SensorInput input) {
		 Alert alert = new Alert();

	        alert.setRule(rule);
	        alert.setSensor(rule.getSensor());
	        alert.setSensorInput(input);
	        alert.setMessage(rule.getNotificationMessage());
	        alert.setTriggeredValue(input.getValue());
	        alert.setTriggeredAt(LocalDateTime.now());
	        alert.setResolved(false);
	        alert.setSeverity(rule.getSeverity());


	        return alertRepository.save(alert);
	}

	@Override
	public Alert resolveAlert(Long alertId) {
		   Alert alert = alertRepository.findById(alertId)
	                .orElseThrow(() -> new RuntimeException("Alert not found"));

	        alert.setResolved(true);

	        return alertRepository.save(alert);
	}

	@Override
	public List<Alert> getBySensor(Long sensorId) {
        return alertRepository.findBySensorId(sensorId);

	}

	@Override
	public List<Alert> getUnresolvedAlerts() {
        return alertRepository.findByIsResolvedFalse();

	}
	
	@Override
	public List<Alert> getAllAlerts() {
	    return alertRepository.findAll();
	}

	@Override
	public long countUnresolved() {
        return alertRepository.countByIsResolvedFalse();

	}
	
	@Override
	public List<Alert> getAlertsByUser(Long ownerId) {
	    return alertRepository.findBySensor_EmbeddedSystem_Owner_Id(ownerId);
	}

	public long countBySeverity(Severity severity) {
	    return alertRepository.countBySeverity(severity);
	}
	
	@Override
	public List<Alert> getUnresolvedAlertsByUser(Long userId) {
	    return alertRepository.findUnresolvedAlertsByOwnerId(userId);
	}
	
	@Override
	public long countUnresolvedByUser(Long userId) {
	    return alertRepository.countBySensor_EmbeddedSystem_Owner_IdAndIsResolvedFalse(userId);
	}


}
