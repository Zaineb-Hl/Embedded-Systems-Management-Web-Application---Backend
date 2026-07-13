package bws.webdevintern.Embedded.system.PFE.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bws.webdevintern.Embedded.system.PFE.models.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long>{

	List<Sensor> findByEmbeddedSystemId(Long systemId);

    List<Sensor> findBySensorTypeId(Long typeId);

    List<Sensor> findByIsActiveTrue();
    
    List<Sensor> findByEmbeddedSystemOwnerId(Long userId);
	
	boolean existsByNameAndEmbeddedSystemId(String name, Long embeddedSystemId);
}
