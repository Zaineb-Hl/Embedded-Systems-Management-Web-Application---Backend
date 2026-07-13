package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bws.webdevintern.Embedded.system.PFE.models.EmbeddedSystem;
import bws.webdevintern.Embedded.system.PFE.models.SystemStatus;
import bws.webdevintern.Embedded.system.PFE.models.User;
import bws.webdevintern.Embedded.system.PFE.repositories.EmbeddedSystemRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.UserRepository;
import bws.webdevintern.Embedded.system.PFE.services.EmbeddedSystemService;
import bws.webdevintern.Embedded.system.PFE.exceptions.ResourceNotFoundException;
import bws.webdevintern.Embedded.system.PFE.exceptions.ForbiddenAccessException;

@Service
public class EmbeddedSystemServiceImpl implements EmbeddedSystemService{

	@Autowired
	private EmbeddedSystemRepository systemRepository;
	@Autowired
    private UserRepository userRepository;


	@Override
	public EmbeddedSystem createSystem(EmbeddedSystem system, Long ownerId) {
		
		 User owner = userRepository.findById(ownerId)
	                .orElseThrow(() -> new RuntimeException("User not found"));
		 
		 if (systemRepository.existsByUniqueIdentifier(system.getUniqueIdentifier())) {
		        throw new RuntimeException("UniqueIdentifier already exists");
		    }

	        system.setOwner(owner);
	        system.setCreatedAt(LocalDateTime.now());
	        system.setStatus(SystemStatus.OFFLINE);

	        return systemRepository.save(system);
	}

	@Override
	public EmbeddedSystem updateSystem(Long id, EmbeddedSystem system) {
		
		EmbeddedSystem existing = systemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("System not found"));

        existing.setName(system.getName());
        existing.setStatus(system.getStatus()); 
        existing.setDescription(system.getDescription());
        existing.setUpdatedAt(LocalDateTime.now());
        return systemRepository.save(existing);
	}

	@Override
	public void deleteEmbeddedSystem(Long Id) {

        systemRepository.deleteById(Id);

	}
	

	@Override
	public List<EmbeddedSystem> getAllEmbeddedSystems() {
        
		return systemRepository.findAll();
	}

	
	@Override
	public EmbeddedSystem getEmbeddedSystemById(Long id) {
	    return systemRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("System not found with id " + id));
	}

	@Override
	public List<EmbeddedSystem> getByOwner(Long ownerId) {
        
		return systemRepository.findByOwnerId(ownerId);

	}

	@Override
	public List<EmbeddedSystem> getByStatus(SystemStatus status) {
        
		return systemRepository.findByStatus(status);

	}

	@Override
	public Optional<EmbeddedSystem> getEmbeddedSystemByUniqueIdentifier(String uniqueIdentifier) {
		return systemRepository.findByUniqueIdentifier(uniqueIdentifier);
	}

	@Override
	public EmbeddedSystem getEmbeddedSystemByIdForUser(Long id, User currentUser) {
	    EmbeddedSystem system = getEmbeddedSystemById(id);
	    checkOwnership(system, currentUser);
	    return system;
	}


@Override
public List<EmbeddedSystem> getByOwnerForUser(Long ownerId, User currentUser) {
    if (!isAdmin(currentUser) && !currentUser.getId().equals(ownerId)) {
        throw new ForbiddenAccessException("You are not allowed to view these systems");
    }
    return getByOwner(ownerId);
}

@Override
public EmbeddedSystem updateSystem(Long id, EmbeddedSystem system, User currentUser) {
    EmbeddedSystem existing = getEmbeddedSystemById(id);
    checkOwnership(existing, currentUser);

    existing.setName(system.getName());
    existing.setStatus(system.getStatus());
    existing.setDescription(system.getDescription());
    existing.setUpdatedAt(LocalDateTime.now());
    return systemRepository.save(existing);
}

@Override
public void deleteEmbeddedSystem(Long id, User currentUser) {
    EmbeddedSystem existing = getEmbeddedSystemById(id);
    checkOwnership(existing, currentUser);
    systemRepository.deleteById(id);
}
	
	private boolean isAdmin(User user) {
	    return user.getRoles().stream()
	            .anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
	}

	private void checkOwnership(EmbeddedSystem system, User currentUser) {
	    if (!isAdmin(currentUser) && !system.getOwner().getId().equals(currentUser.getId())) {
	        throw new ForbiddenAccessException("You are not allowed to access this system");
	    }
	}
}
