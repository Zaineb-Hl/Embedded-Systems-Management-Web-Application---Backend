package bws.webdevintern.Embedded.system.PFE.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import bws.webdevintern.Embedded.system.PFE.models.EmbeddedSystem;
import bws.webdevintern.Embedded.system.PFE.models.Sensor;
import bws.webdevintern.Embedded.system.PFE.models.SensorConfiguration;
import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
import bws.webdevintern.Embedded.system.PFE.models.SystemStatus;
import bws.webdevintern.Embedded.system.PFE.repositories.EmbeddedSystemRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorConfigurationRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorInputRepository;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorRepository;

@Component
public class SystemStatusCheckScheduler {


    @Autowired
    private EmbeddedSystemRepository systemRepository;
    @Autowired
    private SensorConfigurationRepository sensorConfigRepository;
    @Autowired
    private SensorInputRepository sensorInputRepository;
    @Autowired
    private SensorRepository sensorRepository; // ← AJOUT

    //@Scheduled(fixedRate = 30000)
    public void checkSystemsStatus() {

    	    List<Sensor> activeSensors = sensorRepository.findByIsActiveTrue();

    	    // Grouper les capteurs par système
    	    Map<Long, List<Sensor>> sensorsBySystem = activeSensors.stream()
    	        .filter(s -> s.getEmbeddedSystem() != null)
    	        .collect(Collectors.groupingBy(s -> s.getEmbeddedSystem().getId()));

    	    for (Map.Entry<Long, List<Sensor>> entry : sensorsBySystem.entrySet()) {

    	        EmbeddedSystem system = systemRepository.findById(entry.getKey()).orElse(null);
    	        if (system == null) continue;

    	        // Ne pas toucher un système en MAINTENANCE et OFFLINE
    	        if (system.getStatus() == SystemStatus.MAINTENANCE || 
    	        	system.getStatus() == SystemStatus.OFFLINE) continue;
    	        
    	        boolean systemHasError = false;

    	        for (Sensor sensor : entry.getValue()) {

    	            Optional<SensorConfiguration> configOpt =
    	                sensorConfigRepository.findBySensorIdAndIsActiveTrue(sensor.getId());

    	            if (configOpt.isEmpty()) continue;

    	            int seuilSilence = 5 * configOpt.get().getSamplingRate();

    	            // ── Condition 1 : Silence ──
    	            Optional<SensorInput> lastInputOpt =
    	                sensorInputRepository.findTopBySensorIdOrderByTimestampDesc(sensor.getId());

    	            if (lastInputOpt.isPresent()) {
    	                LocalDateTime limite = LocalDateTime.now().minusSeconds(seuilSilence);
    	                if (lastInputOpt.get().getTimestamp().isBefore(limite)) {
    	                    sensor.setIsActive(false);
    	                    sensorRepository.save(sensor);
    	                    systemHasError = true;
    	                    break;
    	                }
    	            } else {
    	                systemHasError = true;
    	                break;
    	            }

    	            // ── Condition 2 : 5 INVALID successifs ──
    	            List<SensorInput> last5 =
    	                sensorInputRepository.findTop5BySensorIdOrderByTimestampDesc(sensor.getId());

    	            if (last5.size() == 5 && last5.stream()
    	                    .allMatch(i -> "INVALID".equals(i.getQuality()))) {
    	                systemHasError = true;
    	                break;
    	            }
    	        }

    	        // ── Mise à jour du statut ──
    	        if (systemHasError && system.getStatus() != SystemStatus.ERROR) {
    	            system.setStatus(SystemStatus.ERROR);
    	            systemRepository.save(system);
    	        } else if (!systemHasError && system.getStatus() == SystemStatus.ERROR) {
    	            system.setStatus(SystemStatus.ONLINE);
    	            systemRepository.save(system);
    	        }
    	    }
    }
}
