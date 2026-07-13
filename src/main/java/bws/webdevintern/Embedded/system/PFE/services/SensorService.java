package bws.webdevintern.Embedded.system.PFE.services;

import java.util.List;

import bws.webdevintern.Embedded.system.PFE.models.Sensor;
import bws.webdevintern.Embedded.system.PFE.models.User;

public interface SensorService {
	
	 	public Sensor createSensor(Sensor sensor, Long systemId, Long typeId);

	 	public Sensor updateSensor(Long id, Sensor sensor, Long systemId, Long typeId);

	 	public Sensor activateSensor(Long id);

	 	public Sensor deactivateSensor(Long id);

	 	public Sensor getById(Long id);

	 	public List<Sensor> getBySystem(Long systemId);

	 	public void deleteSensor(Long id);
	 	
	 	public List<Sensor> getByUser(Long userId);
	 	
	 	public List<Sensor> getAllSensors();
	 	


	    // lecture SÉCURISÉE — c'est celle-ci que le contrôleur doit appeler
	    public Sensor getByIdForUser(Long id, User currentUser);
	    public List<Sensor> getBySystemForUser(Long systemId, User currentUser);

}
