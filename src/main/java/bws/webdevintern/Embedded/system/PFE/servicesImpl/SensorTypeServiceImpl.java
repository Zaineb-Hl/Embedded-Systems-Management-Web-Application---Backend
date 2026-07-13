package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bws.webdevintern.Embedded.system.PFE.models.SensorType;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorTypeRepository;
import bws.webdevintern.Embedded.system.PFE.services.SensorTypeService;

@Service
public class SensorTypeServiceImpl implements SensorTypeService {

	@Autowired
    private SensorTypeRepository sensorTypeRepository;

	
	@Override
	public SensorType create(SensorType type) {
        return sensorTypeRepository.save(type);

	}

	@Override
	public List<SensorType> getAll() {
        return sensorTypeRepository.findAll();

	}
	
    @Override
    public SensorType getSensorTypeById(Long id) {
        return sensorTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SensorType not found: " + id));
    }

    @Override
    @Transactional
    public String delete(Long id) {
        sensorTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sensor type not found with id: " + id));
        
        try {
            sensorTypeRepository.deleteById(id);
            sensorTypeRepository.flush(); // force Hibernate a executer le DELETE immediatement dans MySQL
            return "Sensor type deleted successfully";
        } catch (Exception e) {
            throw new RuntimeException(
                "This sensor type is linked to existing sensors. Please delete the associated sensors first."
            );
        }
    }

	@Override
	@Transactional
	public SensorType updateSensorType(Long id, SensorType type) {

	        SensorType existing = sensorTypeRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("SensorType not found: " + id));
	        existing.setCode(type.getCode());
	        existing.setDescription(type.getDescription());
	        existing.setUnit(type.getUnit());
	        return sensorTypeRepository.save(existing);
	    
	}

}
