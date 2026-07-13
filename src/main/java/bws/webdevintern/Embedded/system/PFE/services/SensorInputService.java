package bws.webdevintern.Embedded.system.PFE.services;

import java.time.LocalDateTime;
import java.util.List;

import bws.webdevintern.Embedded.system.PFE.models.SensorInput;


public interface SensorInputService {


	public SensorInput saveInput(Long sensorId, SensorInput input);
	
	public List<SensorInput> getBySensor(Long sensorId);
		
	public List<SensorInput> getLatestValues(Long sensorId);
	
	 List<SensorInput> getBetweenDates(Long sensorId,
             LocalDateTime start,
             LocalDateTime end);


}
