package bws.webdevintern.Embedded.system.PFE.services;

import java.util.List;

import bws.webdevintern.Embedded.system.PFE.models.Rule;
import bws.webdevintern.Embedded.system.PFE.models.User;

public interface RuleService {
	
	public Rule createRule(Rule rule, Long sensorId);

    public Rule updateRule(Long id, Rule rule,  Long sensorId);

    public Rule activateRule(Long id);

    public Rule deactivateRule(Long id);
    
    public Rule getRuleById(Long id);
    
    public List<Rule> getAllRules();

    public List<Rule> getBySensor(Long sensorId);

    public void deleteRule(Long id);
    
    // lecture sécurisée — utilisée par le contrôleur
    public Rule getRuleByIdForUser(Long id, User currentUser);
    public List<Rule> getBySensorForUser(Long sensorId, User currentUser);
    public List<Rule> getByUser(Long userId);


}
