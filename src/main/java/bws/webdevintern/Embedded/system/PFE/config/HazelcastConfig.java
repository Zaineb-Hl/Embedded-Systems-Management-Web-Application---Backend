package bws.webdevintern.Embedded.system.PFE.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {
	@Bean(name = "hazelcastConfigCustom")
	public Config hazelcastConfig() {
		Config config = new Config();
		config.setInstanceName("hazelcast-instance");
		
		config.getMapConfig("sensor-map").setTimeToLiveSeconds(3600);
		
		// Map 1 : dernière valeur par capteur sensor-latest
        MapConfig latestMap = new MapConfig("sensor-latest");
        latestMap.setTimeToLiveSeconds(300);   // expire après 5 min sans mise à jour
        latestMap.setBackupCount(1);
        config.addMapConfig(latestMap);

        // Map 2 : buffer à persister en MySQL sensor-buffer
        // Cette map est un buffer temporaire qui stocke les mesures en attente
        // d'être enregistrées dans MySQL (quand MySQL est temporairement indisponible).
        // Le SensorDataPersistenceJob vide ce buffer toutes les 30 secondes.
        MapConfig bufferMap = new MapConfig("sensor-buffer");
        bufferMap.setBackupCount(1);
        
        // Configuration de l'éviction pour éviter que le buffer
        // grossisse infiniment et consomme toute la mémoire RAM
        EvictionConfig eviction = new EvictionConfig();
        // Supprime les mesures les plus ANCIENNES en premier
        // Plus logique : si une mesure très ancienne n'a pas pu être persistée
        // depuis longtemps, c'est moins critique que les récentes
        eviction.setEvictionPolicy(EvictionPolicy.LRU);
        eviction.setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        eviction.setSize(10000);               
        bufferMap.setEvictionConfig(eviction);
        config.addMapConfig(bufferMap);
        
        
		return config;
	}
}