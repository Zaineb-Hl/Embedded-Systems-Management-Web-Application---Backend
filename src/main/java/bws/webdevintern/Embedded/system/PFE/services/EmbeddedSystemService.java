package bws.webdevintern.Embedded.system.PFE.services;

import java.util.List;
import java.util.Optional;

import bws.webdevintern.Embedded.system.PFE.models.EmbeddedSystem;
import bws.webdevintern.Embedded.system.PFE.models.SystemStatus;
import bws.webdevintern.Embedded.system.PFE.models.User;

public interface EmbeddedSystemService {
	
    public EmbeddedSystem createSystem(EmbeddedSystem system, Long ownerId);
    
    public EmbeddedSystem updateSystem(Long id, EmbeddedSystem system);
		
	public void deleteEmbeddedSystem(Long Id);
	
	public List<EmbeddedSystem> getAllEmbeddedSystems();
	
	public EmbeddedSystem getEmbeddedSystemById(Long Id);
	
    public List<EmbeddedSystem> getByOwner(Long ownerId);

    public List<EmbeddedSystem> getByStatus(SystemStatus status);
    	
	Optional<EmbeddedSystem> getEmbeddedSystemByUniqueIdentifier(String uniqueIdentifier);
	
	EmbeddedSystem getEmbeddedSystemByIdForUser(Long id, User currentUser);
	List<EmbeddedSystem> getByOwnerForUser(Long ownerId, User currentUser);
	EmbeddedSystem updateSystem(Long id, EmbeddedSystem system, User currentUser);
	void deleteEmbeddedSystem(Long id, User currentUser);

    



}
