package bws.webdevintern.Embedded.system.PFE.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bws.webdevintern.Embedded.system.PFE.models.SensorInput;

@Repository
public interface SensorInputRepository extends JpaRepository<SensorInput, Long> {

	 List<SensorInput> findBySensorIdOrderByTimestampDesc(Long sensorId);
	 
	 //recuperer le dernier input d'un capteur 
	 Optional<SensorInput> findTopBySensorIdOrderByTimestampDesc(Long sensorId);
	 List<SensorInput> findTop5BySensorIdOrderByTimestampDesc(Long sensorId);

	 List<SensorInput> findTop50BySensorIdOrderByTimestampDesc(Long sensorId);
	 
	    List<SensorInput> findBySensorIdAndTimestampBetween(
	            Long sensorId,
	            LocalDateTime start,
	            LocalDateTime end
	    );

	 void deleteBySensorId(Long sensorId);
	 
	 long countBySensorId(Long sensorId);
}
