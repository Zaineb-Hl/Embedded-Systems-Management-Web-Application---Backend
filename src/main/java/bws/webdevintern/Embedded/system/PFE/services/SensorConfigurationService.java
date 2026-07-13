package bws.webdevintern.Embedded.system.PFE.services;

import java.util.List;

import bws.webdevintern.Embedded.system.PFE.models.SensorConfiguration;

public interface SensorConfigurationService {

	public SensorConfiguration createConfiguration(Long sensorId, SensorConfiguration configuration);
	
	public SensorConfiguration editSensorConfiguration(Long id, SensorConfiguration config, Long sensorId);	
	
    public SensorConfiguration activateConfiguration(Long configId);
    
    public SensorConfiguration deactivateConfiguration(Long configId);
    
    public List<SensorConfiguration>getAllSensorConfig();

    public List<SensorConfiguration> getBySensor(Long sensorId);
    
    public SensorConfiguration getSensorConfigById(Long configId);

    public String deleteConfiguration(Long id);
}
