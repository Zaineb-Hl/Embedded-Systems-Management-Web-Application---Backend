package bws.webdevintern.Embedded.system.PFE.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bws.webdevintern.Embedded.system.PFE.models.Alert;
import bws.webdevintern.Embedded.system.PFE.models.Severity;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

	 	List<Alert> findBySensorId(Long sensorId);

	    List<Alert> findByRuleId(Long ruleId);

	    List<Alert> findByIsResolvedFalse();

	    List<Alert> findBySeverity(Severity severity);

	    long countByIsResolvedFalse();

	    long countBySeverity(Severity severity);
	    
	    List<Alert> findBySensor_EmbeddedSystem_Owner_Id(Long ownerId);
	    

	    
	    List<Alert> findBySensor_EmbeddedSystem_Owner_IdAndResolvedFalse(Long ownerId);

	    long countBySensor_EmbeddedSystem_Owner_IdAndIsResolvedFalse(Long ownerId);

	    @Query("SELECT a FROM Alert a LEFT JOIN a.sensor s LEFT JOIN s.embeddedSystem e LEFT JOIN e.owner o WHERE a.isResolved = false AND o.id = :ownerId")
	    List<Alert> findUnresolvedAlertsByOwnerId(@Param("ownerId") Long ownerId);
	    
	    void deleteByRuleId(Long ruleId);
}
