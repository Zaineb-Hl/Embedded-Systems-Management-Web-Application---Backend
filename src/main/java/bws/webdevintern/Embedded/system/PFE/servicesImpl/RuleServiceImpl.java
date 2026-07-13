package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bws.webdevintern.Embedded.system.PFE.exceptions.ForbiddenAccessException;
import bws.webdevintern.Embedded.system.PFE.exceptions.ResourceNotFoundException;
import bws.webdevintern.Embedded.system.PFE.models.Rule;
import bws.webdevintern.Embedded.system.PFE.models.Sensor;
import bws.webdevintern.Embedded.system.PFE.models.SensorConfiguration;
import bws.webdevintern.Embedded.system.PFE.models.User;
import bws.webdevintern.Embedded.system.PFE.repositories.AlertRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.RuleRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorConfigurationRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.UserRepository;
import bws.webdevintern.Embedded.system.PFE.services.RuleService;
import jakarta.transaction.Transactional;

@Service
public class RuleServiceImpl implements RuleService {

	@Autowired
	private RuleRepository ruleRepository;
	@Autowired
    private SensorRepository sensorRepository;
	@Autowired
	private SensorConfigurationRepository configRepository;
	@Autowired
	private AlertRepository alertRepository;
	@Autowired
	private UserRepository userRepository;

	private boolean isAdmin(User user) {
	    return user.getRoles().stream()
	            .anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
	}

	private void checkSensorOwnership(Sensor sensor, User currentUser) {
	    Long ownerId = sensor.getEmbeddedSystem().getOwner().getId();
	    if (!isAdmin(currentUser) && !ownerId.equals(currentUser.getId())) {
	        throw new ForbiddenAccessException("You are not allowed to use this sensor");
	    }
	}

	private void checkRuleOwnership(Rule rule, User currentUser) {
	    checkSensorOwnership(rule.getSensor(), currentUser);
	}
	
	@Override
	public Rule createRule(Rule rule, Long sensorId) {
		 Sensor sensor = sensorRepository.findById(sensorId)
	                .orElseThrow(() -> new RuntimeException("Sensor not found"));
		 
		 // Récupérer la configuration active du capteur
		    List<SensorConfiguration> configs = configRepository.findBySensorId(sensorId);
		    SensorConfiguration activeConfig = configs.stream()
		            .filter(SensorConfiguration::isActive)
		            .findFirst()
		            .orElse(null);
		    
		 // Valider le seuil par rapport à la plage du capteur
		    if (activeConfig != null) {
		        double threshold = rule.getThresholdValue();
		        double min = activeConfig.getMinRange();
		        double max = activeConfig.getMaxRange();

		        if (threshold < min || threshold > max) {
		            throw new RuntimeException(
		                "The threshold value " + threshold +
		                " is outside the allowed sensor range  [" + min + ", " + max + "]"
		            );
		        }
		    }

		        rule.setSensor(sensor);
		        rule.setActive(true);

		        return ruleRepository.save(rule);
	}
	
	@Override
	public List<Rule> getAllRules() {
	    return ruleRepository.findAll();
	}
	
	@Override
	public Rule getRuleById(Long id) {
	    return ruleRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Rule not found: " + id));
	}
	
	@Override
	public Rule updateRule(Long id, Rule rule, Long sensorId) {
	    Rule existing = ruleRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Rule not found: " + id));

	    // Déterminer le capteur concerné (nouveau ou existant)
	    Sensor sensor = existing.getSensor();
	    if (sensorId != null) {
	        sensor = sensorRepository.findById(sensorId)
	                .orElseThrow(() -> new RuntimeException("Sensor not found: " + sensorId));
	        existing.setSensor(sensor);
	    }

	    // Valider le seuil par rapport à la plage de la config active du capteur
	    if (sensor != null) {
	        SensorConfiguration activeConfig = configRepository
	                .findBySensorId(sensor.getId())
	                .stream()
	                .filter(SensorConfiguration::isActive)
	                .findFirst()
	                .orElse(null);

	        if (activeConfig != null) {
	            double threshold = rule.getThresholdValue();
	            double min = activeConfig.getMinRange();
	            double max = activeConfig.getMaxRange();

	            if (threshold < min || threshold > max) {
	                throw new RuntimeException(
	                    "Le seuil " + threshold +
	                    " est hors de la plage du capteur [" + min + ", " + max + "]"
	                );
	            }
	        }
	    }

	    // Appliquer les modifications
	    existing.setName(rule.getName());
	    existing.setOperator(rule.getOperator());
	    existing.setThresholdValue(rule.getThresholdValue());
	    existing.setNotificationMessage(rule.getNotificationMessage());

	    return ruleRepository.save(existing);
	}

	@Override
	public Rule activateRule(Long id) {
		Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        rule.setActive(true);

        return ruleRepository.save(rule);
	}

	@Override
	public Rule deactivateRule(Long id) {
		Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        rule.setActive(false);

        return ruleRepository.save(rule);
	}

	@Override
	public List<Rule> getBySensor(Long id) {
        return ruleRepository.findBySensorId(id);

	}

	@Override
	@Transactional
	public void deleteRule(Long id) {
		alertRepository.deleteByRuleId(id); 
        ruleRepository.deleteById(id);
		
	}
	
	@Override
	public Rule getRuleByIdForUser(Long id, User currentUser) {
	    Rule rule = getRuleById(id);
	    checkRuleOwnership(rule, currentUser);
	    return rule;
	}

	@Override
	public List<Rule> getBySensorForUser(Long sensorId, User currentUser) {
	    Sensor sensor = sensorRepository.findById(sensorId)
	            .orElseThrow(() -> new ResourceNotFoundException("Sensor not found"));
	    checkSensorOwnership(sensor, currentUser);
	    return getBySensor(sensorId);
	}
	
	@Override
	public List<Rule> getByUser(Long userId) {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
	    return ruleRepository.findBySensorEmbeddedSystemOwnerId(user.getId());
	}


}
