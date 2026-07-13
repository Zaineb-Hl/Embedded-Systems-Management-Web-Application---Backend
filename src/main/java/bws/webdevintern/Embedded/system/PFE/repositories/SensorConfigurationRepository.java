package bws.webdevintern.Embedded.system.PFE.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bws.webdevintern.Embedded.system.PFE.models.SensorConfiguration;

@Repository
public interface SensorConfigurationRepository extends JpaRepository<SensorConfiguration, Long>{

    List<SensorConfiguration> findBySensorId(Long sensorId);
    Optional<SensorConfiguration> findBySensorIdAndIsActiveTrue(Long sensorId);


}
