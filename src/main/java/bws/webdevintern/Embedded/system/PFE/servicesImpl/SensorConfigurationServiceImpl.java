package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bws.webdevintern.Embedded.system.PFE.models.Sensor;
import bws.webdevintern.Embedded.system.PFE.models.SensorConfiguration;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorConfigurationRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorRepository;
import bws.webdevintern.Embedded.system.PFE.services.SensorConfigurationService;

@Service
public class SensorConfigurationServiceImpl implements SensorConfigurationService{

	@Autowired
	private SensorConfigurationRepository configRepository;
	@Autowired
    private SensorRepository sensorRepository;

	
	@Override
	public SensorConfiguration createConfiguration(Long sensorId, SensorConfiguration configuration) {
	    
	    Sensor sensor = sensorRepository.findById(sensorId)
	            .orElseThrow(() -> new RuntimeException("Sensor not found"));
	    configuration.setSensor(sensor);
	    
	    // une seule configuration doit etre active par capteur , si la nouvelle config est active il faut désactiver l'ancienne
	    if (Boolean.TRUE.equals(configuration.isActive())) {
	        List<SensorConfiguration> existing = 
	            configRepository.findBySensorId(sensorId);
	        
	        for (SensorConfiguration old : existing) {
	            old.setActive(false);
	        }
	        configRepository.saveAll(existing);
	    }
	    
	    return configRepository.save(configuration);
	}

	
	@Override
	public SensorConfiguration editSensorConfiguration(Long id, SensorConfiguration config, Long sensorId) {
	    SensorConfiguration existingConfig = configRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Configuration not found"));

	    existingConfig.setSamplingRate(config.getSamplingRate());
	    existingConfig.setMinRange(config.getMinRange());
	    existingConfig.setMaxRange(config.getMaxRange());
	    existingConfig.setActive(config.isActive());

	    if (sensorId != null) {
	        Sensor sensor = sensorRepository.findById(sensorId)
	                .orElseThrow(() -> new RuntimeException("Sensor not found"));
	        existingConfig.setSensor(sensor);
	    }

	    // Si on active cette config, désactiver les autres du même sensor
	    if (Boolean.TRUE.equals(config.isActive())) {
	        Long sid = sensorId != null ? sensorId : existingConfig.getSensor().getId();
	        List<SensorConfiguration> others = configRepository.findBySensorId(sid);
	        for (SensorConfiguration other : others) {
	            if (!other.getId().equals(id)) { // ne pas toucher celle qu'on est en train d'éditer
	                other.setActive(false);
	            }
	        }
	        configRepository.saveAll(others);
	    }

	    return configRepository.save(existingConfig);
	}


	@Override
	public SensorConfiguration activateConfiguration(Long configId) {
	    SensorConfiguration config = configRepository.findById(configId)
	            .orElseThrow(() -> new RuntimeException("Configuration not found"));

	    // Desactiver toutes les autres configs du même capteur
	    Long sensorId = config.getSensor().getId();
	    List<SensorConfiguration> others = configRepository.findBySensorId(sensorId);
	    for (SensorConfiguration other : others) {
	        if (!other.getId().equals(configId)) {
	            other.setActive(false);
	        }
	    }
	    configRepository.saveAll(others);

	    // Activer celle-ci
	    config.setActive(true);
	    return configRepository.save(config);
	}
	
	@Override
	public SensorConfiguration deactivateConfiguration(Long configId) {
	    SensorConfiguration config = configRepository.findById(configId)
	            .orElseThrow(() -> new RuntimeException("Configuration not found"));
	    config.setActive(false);
	    return configRepository.save(config);
	}

	@Override
	public List<SensorConfiguration> getBySensor(Long sensorId) {
        return configRepository.findBySensorId(sensorId);

	}
	
	@Override
	public List<SensorConfiguration>getAllSensorConfig(){
		return configRepository.findAll();
	}

	@Override
	public String deleteConfiguration(Long id) {
		SensorConfiguration config = configRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Configuration not found with id: " + id));

	    configRepository.delete(config);

	    return "Configuration deleted successfully with id: " + id;
		
	}

	@Override
	public SensorConfiguration getSensorConfigById(Long configId) {
		return configRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Sensor configuration not found"));
	}

}
