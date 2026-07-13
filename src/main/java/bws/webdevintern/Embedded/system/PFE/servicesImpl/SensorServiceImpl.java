package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bws.webdevintern.Embedded.system.PFE.exceptions.ForbiddenAccessException;
import bws.webdevintern.Embedded.system.PFE.exceptions.ResourceNotFoundException;
import bws.webdevintern.Embedded.system.PFE.models.EmbeddedSystem;
import bws.webdevintern.Embedded.system.PFE.models.Sensor;
import bws.webdevintern.Embedded.system.PFE.models.SensorType;
import bws.webdevintern.Embedded.system.PFE.models.SystemStatus;
import bws.webdevintern.Embedded.system.PFE.models.User;
import bws.webdevintern.Embedded.system.PFE.repositories.EmbeddedSystemRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorTypeRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.UserRepository;
import bws.webdevintern.Embedded.system.PFE.services.SensorService;

@Service
public class SensorServiceImpl implements SensorService{

	@Autowired
	private SensorRepository sensorRepository;
	@Autowired
    private EmbeddedSystemRepository systemRepository;
	@Autowired
    private SensorTypeRepository typeRepository;
	@Autowired
	private UserRepository userRepository;


	// ── Utilitaires de sécurité ──────────────────────────────
	private boolean isAdmin(User user) {
	    return user.getRoles().stream()
	            .anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
	}
	
	private void checkOwnership(Sensor sensor, User currentUser) {
	    Long ownerId = sensor.getEmbeddedSystem().getOwner().getId();
	    if (!isAdmin(currentUser) && !ownerId.equals(currentUser.getId())) {
	        throw new ForbiddenAccessException("You are not allowed to access this sensor");
	    }
	}

	private void checkSystemOwnership(EmbeddedSystem system, User currentUser) {
	    if (!isAdmin(currentUser) && !system.getOwner().getId().equals(currentUser.getId())) {
	        throw new ForbiddenAccessException("You are not allowed to use this system");
	    }
	}
	
	@Override
	public Sensor createSensor(Sensor sensor, Long systemId, Long typeId) {
        EmbeddedSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new RuntimeException("System not found"));

        SensorType type = typeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Sensor type not found"));
        
        if (sensor.getName() == null || sensor.getName().trim().isEmpty()) {
            throw new RuntimeException("Sensor name cannot be empty");
        }

        boolean nameExists = sensorRepository
                .existsByNameAndEmbeddedSystemId(sensor.getName().trim(), systemId);
        if (nameExists) {
            throw new RuntimeException(
                "A sensor with name '" + sensor.getName() + "' already exists in this system"
            );
        }
        
        if (system.getStatus() == SystemStatus.MAINTENANCE) {
            throw new RuntimeException(
                "Cannot add a sensor to a system that is under maintenance"
            );
        }
        
        sensor.setEmbeddedSystem(system);
        sensor.setSensorType(type);
        sensor.setIsActive(true);

        return sensorRepository.save(sensor);
	}

	@Override
	public Sensor updateSensor(Long id, Sensor sensor, Long systemId, Long typeId) {
		Sensor existing = sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        existing.setName(sensor.getName());
        existing.setIsActive(sensor.getIsActive());
        
        if(systemId != null) {
            EmbeddedSystem system = systemRepository.findById(systemId)
                    .orElseThrow(() -> new RuntimeException("System not found"));
            existing.setEmbeddedSystem(system);
        }
        
        if(typeId != null) {
            SensorType type = typeRepository.findById(typeId)
                    .orElseThrow(() -> new RuntimeException("Sensor type not found"));
            existing.setSensorType(type);
        }


        return sensorRepository.save(existing);
	}

	@Override
	public Sensor activateSensor(Long id) {
		
		Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        sensor.setIsActive(true);

        return sensorRepository.save(sensor);
	}

	@Override
	public Sensor deactivateSensor(Long id) {
		 Sensor sensor = sensorRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Sensor not found"));

	        sensor.setIsActive(false);

	        return sensorRepository.save(sensor);
	}

	@Override
	public Sensor getById(Long id) {
		 return sensorRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Sensor not found"));
	}

	@Override
	public List<Sensor> getBySystem(Long systemId) {
		return sensorRepository.findByEmbeddedSystemId(systemId);
	}

	@Override
	public void deleteSensor(Long id) {
        sensorRepository.deleteById(id);
		
	}
	
	@Override
	public List<Sensor> getByUser(Long userId) {
		
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    return sensorRepository.findByEmbeddedSystemOwnerId(user.getId());
	}
	
	@Override
	public List<Sensor> getAllSensors() {
	    return sensorRepository.findAll();
	}
	// ── Lecture sécurisée (utilisée par le contrôleur) ───────
		@Override
		public Sensor getByIdForUser(Long id, User currentUser) {
			Sensor sensor = getById(id);
			checkOwnership(sensor, currentUser);
			return sensor;
		}

		@Override
		public List<Sensor> getBySystemForUser(Long systemId, User currentUser) {
			EmbeddedSystem system = systemRepository.findById(systemId)
			        .orElseThrow(() -> new ResourceNotFoundException("System not found"));
			checkSystemOwnership(system, currentUser);
			return getBySystem(systemId);
		}

}
