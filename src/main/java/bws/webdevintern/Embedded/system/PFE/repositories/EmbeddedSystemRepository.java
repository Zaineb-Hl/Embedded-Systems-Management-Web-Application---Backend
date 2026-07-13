package bws.webdevintern.Embedded.system.PFE.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bws.webdevintern.Embedded.system.PFE.models.EmbeddedSystem;
import bws.webdevintern.Embedded.system.PFE.models.SystemStatus;

@Repository
public interface EmbeddedSystemRepository extends JpaRepository<EmbeddedSystem, Long>{

    List<EmbeddedSystem> findByOwnerId(Long ownerId);
    
    Optional<EmbeddedSystem> findByUniqueIdentifier(String uniqueIdentifier);
    
    boolean existsByUniqueIdentifier(String uniqueIdentifier);

    List<EmbeddedSystem> findByStatus(SystemStatus status);



}
