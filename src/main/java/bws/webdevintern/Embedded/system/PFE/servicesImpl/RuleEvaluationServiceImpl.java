package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bws.webdevintern.Embedded.system.PFE.models.ComparisonOperator;
import bws.webdevintern.Embedded.system.PFE.models.Rule;
import bws.webdevintern.Embedded.system.PFE.models.SensorConfiguration;
import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
import bws.webdevintern.Embedded.system.PFE.repositories.RuleRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorConfigurationRepository;
import bws.webdevintern.Embedded.system.PFE.services.AlertService;
import bws.webdevintern.Embedded.system.PFE.services.RuleEvaluationService;

@Service
public class RuleEvaluationServiceImpl implements RuleEvaluationService{

	@Autowired
	 private RuleRepository ruleRepository;
	@Autowired
	 private AlertService alertService;
	@Autowired
	private SensorConfigurationRepository sensorConfigurationRepository;
	
	

    /**
     * Calcule la qualité de la mesure EN MÉMOIRE.
     *
     * Cette méthode est appelée AVANT le save() dans SensorInputServiceImpl.
     * Elle vérifie si la valeur reçue est dans la plage physique autorisée
     * pour ce capteur (minRange ≤ valeur ≤ maxRange).
     *
     * Résultats possibles :
     *   - Valeur hors plage → input.quality = "INVALID"
     *   - Valeur dans la plage → input.quality reste null (= valide)
     *   - Aucune configuration active → on ne peut pas vérifier → quality reste null
     *
     * Aucune écriture en base de données dans cette méthode.
     */
    @Override
    public void calculerQuality(SensorInput input) {
 
        // Récupérer toutes les configurations du capteur
        List<SensorConfiguration> configs = sensorConfigurationRepository
                .findBySensorId(input.getSensor().getId());
 
        // Chercher la configuration active parmi toutes les configurations
        SensorConfiguration configActive = configs.stream()
                .filter(SensorConfiguration::isActive)
                .findFirst()
                .orElse(null);
 
        // Si aucune configuration active n'est trouvée, on ne peut pas valider
        // la plage physique → on laisse quality à null et on continue
        if (configActive == null) {
            return;
        }
 
        double valeur = input.getValue();
        double valeurMin = configActive.getMinRange();
        double valeurMax = configActive.getMaxRange();
 
        // Si la valeur est en dehors de la plage physique autorisée
        // → la mesure est considérée comme corrompue ou capteur défaillant
        if (valeur < valeurMin || valeur > valeurMax) {
            input.setQuality("INVALID");
            // On s'arrête ici — aucune règle ne sera évaluée sur une mesure invalide
        }
 
        // Si la valeur est dans la plage → quality reste null (= mesure valide)
    }
	
    /**
     * Évalue les règles actives du capteur et crée les alertes si nécessaire.
     *
     * Cette méthode est appelée APRÈS le save() dans SensorInputServiceImpl.
     * À ce stade, input.getId() est renseigné par MySQL, ce qui permet à
     * alertService.createAlert() de créer une alerte avec la clé étrangère
     * sensor_input_id correctement définie.
     *
     * Si quality = INVALID → aucune règle n'est évaluée, on sort immédiatement.
     * Sinon → on vérifie chaque règle active et on crée une alerte si le seuil
     * est dépassé.
     */
    @Override
    @Transactional
    public void evaluate(SensorInput input) {
 
        // Si la mesure est invalide (hors plage physique), on ne déclenche
        // aucune règle. Une mesure corrompue ne doit pas générer d'alerte.
        if ("INVALID".equals(input.getQuality())) {
            return;
        }
 
        // Récupérer toutes les règles configurées pour ce capteur
        List<Rule> regles = ruleRepository.findBySensorId(
                input.getSensor().getId()
        );
 
        // Évaluer chaque règle une par une
        for (Rule regle : regles) {
 
            // N'évaluer que les règles actives
            // et vérifier si la valeur reçue satisfait la condition de la règle
            if (Boolean.TRUE.equals(regle.isActive())
                    && conditionEstVerifiee(regle, input.getValue())) {
 
                // La condition est vérifiée → créer une alerte en base de données
                // input.getId() existe car save() a été appelé avant evaluate()
                // → pas d'erreur TransientPropertyValueException
                alertService.createAlert(regle, input);
            }
        }
    }
	
    /**
     * Vérifie si la valeur mesurée satisfait la condition définie dans une règle.
     *
     * Exemple : si la règle dit "valeur > 30" et que la mesure est 35 → true
     *           si la règle dit "valeur > 30" et que la mesure est 25 → false
     */
    private boolean conditionEstVerifiee(Rule regle, double valeur) {
 
        ComparisonOperator operateur = regle.getOperator();
        double seuil = regle.getThresholdValue();
 
        switch (operateur) {
 
            case GREATER_THAN:
                // Déclencher si la valeur est strictement supérieure au seuil
                return valeur > seuil;
 
            case LESS_THAN:
                // Déclencher si la valeur est strictement inférieure au seuil
                return valeur < seuil;
 
            case EQUAL:
                // Déclencher si la valeur est exactement égale au seuil
                return valeur == seuil;
 
            case GREATER_OR_EQUAL:
                // Déclencher si la valeur est supérieure ou égale au seuil
                return valeur >= seuil;
 
            case LESS_OR_EQUAL:
                // Déclencher si la valeur est inférieure ou égale au seuil
                return valeur <= seuil;
 
            case NOT_EQUAL:
                // Déclencher si la valeur est différente du seuil
                return valeur != seuil;
 
            default:
                // Opérateur inconnu → on ne déclenche rien
                return false;
        }
    }
}
