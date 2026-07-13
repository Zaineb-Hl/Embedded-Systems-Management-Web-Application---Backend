package bws.webdevintern.Embedded.system.PFE.servicesImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
import bws.webdevintern.Embedded.system.PFE.services.SensorCacheService;

@Service
public class SensorCacheServiceImpl implements SensorCacheService{

	 private static final String LATEST_MAP = "sensor-latest";
	 private static final String BUFFER_MAP = "sensor-buffer";
	 
	 @Autowired
	 private HazelcastInstance hazelcastInstance;
	
	    /** Stocker/écraser la dernière valeur d'un capteur dans sensor-latest */
	 @Override
	    public void cacheLatestValue(Long sensorId, SensorInput input) {
	        IMap<String, SensorInput> map = hazelcastInstance.getMap(LATEST_MAP);
	        map.put(latestKey(sensorId), input);
	    }

	    /** Lire la dernière valeur d'un capteur depuis sensor-latest (sans toucher MySQL) */
	    @Override
	    public SensorInput getLatestFromCache(Long sensorId) {
	        IMap<String, SensorInput> map = hazelcastInstance.getMap(LATEST_MAP);
	        return map.get(latestKey(sensorId));
	    }
	
	


	    // ── sensor-buffer : write-buffer persistence asynchrone ─────────────────
	 
	    @Override
	    public void addToBuffer(SensorInput input) {
	        IMap<String, SensorInput> map = hazelcastInstance.getMap(BUFFER_MAP);
	        map.put(bufferKey(input), input);
	    }
	 
	    /**
	     * Retire une entrée du buffer après que MySQL l'a persistée avec succès.
	     * Évite le doublon au prochain flush de SensorDataPersistenceJob.
	     */
	    @Override
	    public void removeFromBuffer(SensorInput input) {
	        IMap<String, SensorInput> map = hazelcastInstance.getMap(BUFFER_MAP);
	        map.remove(bufferKey(input));
	    }
	 
	    @Override
	    public List<SensorInput> drainBuffer() {
	        IMap<String, SensorInput> map = hazelcastInstance.getMap(BUFFER_MAP);
	        Set<String> keys = map.keySet();
	        List<SensorInput> result = new ArrayList<>();
	        for (String key : keys) {
	            SensorInput input = map.remove(key); 
	            if (input != null) {
	                result.add(input);
	            }
	        }
	        return result;
	    }
	 
	    @Override
	    public int getBufferSize() {
	        return hazelcastInstance.getMap(BUFFER_MAP).size();
	    }
	 
	    // ── Helpers clé ─────────────────────────────────────────────────────────
	 
	    private String latestKey(Long sensorId) {
	        return "sensor:" + sensorId;
	    }
	 
	    /**
	     * Clé unique dans le buffer : sensorId + timestamp ISO.
	     * Partagée entre addToBuffer() et removeFromBuffer() — ne pas modifier l'une sans l'autre.
	     */
	    private String bufferKey(SensorInput input) {
	        return "buf:" + input.getSensor().getId() + ":" + input.getTimestamp().toString();
	    }
	
}


