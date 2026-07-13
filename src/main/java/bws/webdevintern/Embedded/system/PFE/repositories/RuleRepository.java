package bws.webdevintern.Embedded.system.PFE.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bws.webdevintern.Embedded.system.PFE.models.Rule;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {


		List<Rule> findBySensorId(Long sensorId);

	    void deleteBySensorId(Long sensorId);

	    List<Rule> findBySensorEmbeddedSystemOwnerId(Long ownerId);
	
}
