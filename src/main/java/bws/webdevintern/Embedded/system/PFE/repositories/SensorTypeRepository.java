package bws.webdevintern.Embedded.system.PFE.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bws.webdevintern.Embedded.system.PFE.models.SensorType;

@Repository
public interface SensorTypeRepository extends JpaRepository<SensorType, Long>{

	 Optional<SensorType> findByCode(String code);

	 boolean existsByCode(String code);
	 
}
