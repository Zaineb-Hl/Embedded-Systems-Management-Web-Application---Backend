package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bws.webdevintern.Embedded.system.PFE.models.EmbeddedSystem;
import bws.webdevintern.Embedded.system.PFE.models.Sensor;
import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
import bws.webdevintern.Embedded.system.PFE.models.SystemStatus;
import bws.webdevintern.Embedded.system.PFE.repositories.EmbeddedSystemRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorInputRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorRepository;
import bws.webdevintern.Embedded.system.PFE.services.RuleEvaluationService;
import bws.webdevintern.Embedded.system.PFE.services.SensorCacheService;
import bws.webdevintern.Embedded.system.PFE.services.SensorInputService;

@Service
public class SensorInputServiceImpl implements SensorInputService{
	
	   private static final Logger log = LoggerFactory.getLogger(SensorInputServiceImpl.class);
	   
	    @Autowired
	    private SensorRepository sensorRepository;
	 
	    @Autowired
	    private SensorInputRepository inputRepository;
	 
	    @Autowired
	    private RuleEvaluationService ruleEvaluationService;
	 
	    @Autowired
	    private EmbeddedSystemRepository systemRepository;
	 
	    @Autowired
	    private SensorCacheService sensorCacheService;
	 
	    @Override
	    public SensorInput saveInput(Long sensorId, SensorInput input) {
	 
	        // ── Étape 1 : Résoudre le capteur ───────────────────────────────────
	        // On cherche le capteur en base de données à partir de son identifiant.
	        // Si le capteur n'existe pas → une exception est levée et le traitement s'arrête.
	        Sensor sensor = sensorRepository.findById(sensorId)
	                .orElseThrow(() -> new RuntimeException("Sensor not found: " + sensorId));
	 
	        input.setSensor(sensor);
	        input.setTimestamp(LocalDateTime.now());
	 
	        // ── Étape 2 : Activer le capteur s'il ne l'est pas déjà ─────────────
	        // Dès qu'un capteur envoie une mesure, il passe automatiquement à isActive=true.
	        // Le try/catch permet de continuer même si MySQL est temporairement indisponible.
	        if (Boolean.FALSE.equals(sensor.getIsActive()) || sensor.getIsActive() == null) {
	            sensor.setIsActive(true);
	            try {
	                sensorRepository.save(sensor);
	            } catch (Exception e) {
	                log.warn("[saveInput] Impossible d'activer le capteur {} : {}", sensorId, e.getMessage());
	            }
	        }
	 
	        // ── Étape 3 : Passer le système en ONLINE ───────────────────────────
	        // Dès qu'un capteur envoie une mesure, le système embarqué parent
	        // passe automatiquement en statut ONLINE.
	        // On ne touche pas au statut si le système est déjà ONLINE ou en MAINTENANCE.
	        EmbeddedSystem system = sensor.getEmbeddedSystem();
	        if (system != null
	                && system.getStatus() != SystemStatus.ONLINE
	                && system.getStatus() != SystemStatus.MAINTENANCE) {
	            system.setStatus(SystemStatus.ONLINE);
	            try {
	                systemRepository.save(system);
	            } catch (Exception e) {
	                log.warn("[saveInput] Impossible de mettre le système {} en ONLINE : {}",
	                        system.getId(), e.getMessage());
	            }
	        }
	            
	   	 
	        // ── Étape 4 : Calcul de la quality EN MÉMOIRE ───────────────────────
	        // On calcule la qualité de la mesure AVANT de la sauvegarder en MySQL.
	        // Pourquoi ? Pour que le INSERT MySQL contienne directement la bonne
	        // valeur de quality → un seul INSERT suffit, pas besoin d'un UPDATE après.
	        //
	        // calculerQuality() vérifie si la valeur est dans la plage physique
	        // autorisée (minRange ≤ valeur ≤ maxRange) :
	        //   - Valeur hors plage → quality = "INVALID"
	        //   - Valeur dans la plage → quality reste null (= mesure valide)
	        //
	        // Aucune alerte n'est créée ici, aucune écriture en base de données.
	        ruleEvaluationService.calculerQuality(input);
	        log.debug("[saveInput] Sensor {} quality calculée en mémoire : {}", sensorId, input.getQuality());
	        
	 
	        // ── Étape 5 : Mise en cache Hazelcast ───────────────────────────────
	        // On stocke immédiatement la dernière valeur dans le cluster Hazelcast
	        // (map sensor-latest) pour alimenter le dashboard temps réel.
	        // Cette étape est faite en premier, avant même MySQL, pour garantir
	        // que le dashboard affiche toujours la valeur la plus récente.
	        sensorCacheService.cacheLatestValue(sensorId, input);
	        log.debug("[saveInput] Sensor {} mis en cache Hazelcast (sensor-latest)", sensorId);

	 
	        // ── Étape 6 : Persistence en MySQL ──────────────────────────────────
	        // On insère la mesure en base de données avec la quality déjà calculée.
	        // Un seul INSERT MySQL — propre et efficace.
	        //
	        // Après cette étape, saved.getId() est renseigné par MySQL.
	        // Cet id est nécessaire pour l'étape 8 (création des alertes).
	        SensorInput saved = input;
	        boolean mysqlOk = false;
	 
	        try {
	            saved = inputRepository.save(input);
	            mysqlOk = true;
	            log.debug("[saveInput] Sensor {} persisté en MySQL (id={}, quality={})",
	                    sensorId, saved.getId(), saved.getQuality());
	        } catch (Exception e) {
	            log.warn("[saveInput] MySQL indisponible pour sensor {} — passage en buffer Hazelcast. Cause : {}",
	                    sensorId, e.getMessage());
	        }
	 
	        // ── Étape 7 : Buffer Hazelcast si MySQL a échoué ────────────────────
	        // Si MySQL était indisponible, on place la mesure dans le buffer Hazelcast
	        // (map sensor-buffer). Le SensorDataPersistenceJob videra ce buffer
	        // automatiquement toutes les 30 secondes dès que MySQL sera accessible.
	        //
	        // On n'évalue pas les règles dans ce cas car saved.getId() est null
	        // → impossible de créer une alerte sans id valide.
	        // Les alertes seront perdues pour ces mesures en cas de panne MySQL.
	        // C'est un compromis acceptable : la mesure est sauvegardée, pas l'alerte.
	        if (!mysqlOk) {
	            sensorCacheService.addToBuffer(input);
	            log.debug("[saveInput] Sensor {} ajouté au buffer Hazelcast", sensorId);
	            return saved;
	        }
	 
	        // ── Étape 8 : Évaluation des règles et création des alertes ─────────
	        // On évalue les règles APRÈS le save() car :
	        //   - saved.getId() existe maintenant (généré par MySQL à l'étape 6)
	        //   - alertService.createAlert() a besoin de cet id pour la clé étrangère
	        //     sensor_input_id dans la table alert
	        //
	        // evaluate() vérifie chaque règle active du capteur :
	        //   - Si quality = INVALID → aucune règle évaluée, aucune alerte
	        //   - Si une condition est vérifiée → alerte créée en base de données
	        ruleEvaluationService.evaluate(saved);
	 
	        return saved;
	    }
	 
	   
	 
	    @Override
	    public List<SensorInput> getBySensor(Long sensorId) {
	        return inputRepository.findBySensorIdOrderByTimestampDesc(sensorId);
	    }
	 
	    @Override
	    public List<SensorInput> getLatestValues(Long sensorId) {
	        return inputRepository.findTop50BySensorIdOrderByTimestampDesc(sensorId);
	    }
	 
	    @Override
	    public List<SensorInput> getBetweenDates(Long sensorId, LocalDateTime start, LocalDateTime end) {
	        return inputRepository.findBySensorIdAndTimestampBetween(sensorId, start, end);
	    }
}
