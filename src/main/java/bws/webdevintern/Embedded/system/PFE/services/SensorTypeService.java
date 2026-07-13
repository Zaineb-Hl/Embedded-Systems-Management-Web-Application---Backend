package bws.webdevintern.Embedded.system.PFE.services;

import java.util.List;

import bws.webdevintern.Embedded.system.PFE.models.SensorType;

public interface SensorTypeService {

	
    SensorType create(SensorType type);

    List<SensorType> getAll();
    
    SensorType getSensorTypeById(Long id);
    
    SensorType updateSensorType(Long id, SensorType type);

    public String delete(Long id);
    

}
