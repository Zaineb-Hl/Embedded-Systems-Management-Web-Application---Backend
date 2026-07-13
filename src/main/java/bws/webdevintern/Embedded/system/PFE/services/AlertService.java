package bws.webdevintern.Embedded.system.PFE.services;

import java.util.List;

import bws.webdevintern.Embedded.system.PFE.models.Alert;
import bws.webdevintern.Embedded.system.PFE.models.Rule;
import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
import bws.webdevintern.Embedded.system.PFE.models.Severity;


public interface AlertService {
	
	public Alert createAlert(Rule rule, SensorInput input);

	public Alert resolveAlert(Long alertId);

	public List<Alert> getBySensor(Long sensorId);

	public List<Alert> getUnresolvedAlerts();
	
	public List<Alert> getAllAlerts();

	public long countUnresolved();

	public long countBySeverity(Severity severity);
	
	public List<Alert> getAlertsByUser(Long ownerId);
	
	public long countUnresolvedByUser(Long userId);
	
	List<Alert> getUnresolvedAlertsByUser(Long userId);

}
