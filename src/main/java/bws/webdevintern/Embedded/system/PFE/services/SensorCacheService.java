package bws.webdevintern.Embedded.system.PFE.services;

import java.util.List;

import bws.webdevintern.Embedded.system.PFE.models.SensorInput;

public interface SensorCacheService {
	
    /** Stocker/écraser la dernière valeur d'un capteur dans sensor-latest */
    void cacheLatestValue(Long sensorId, SensorInput input);
 
    /** Lire la dernière valeur d'un capteur depuis sensor-latest (sans toucher MySQL) */
    SensorInput getLatestFromCache(Long sensorId);
 
    /** Ajouter une valeur dans sensor-buffer en attente de persistence MySQL */
    void addToBuffer(SensorInput input);
 
    /**
     * Retirer une entrée spécifique de sensor-buffer après sa persistence MySQL réussie.
     *
     * Utilise la même clé que addToBuffer() : "buf:<sensorId>:<timestamp>"
     * Appelé par SensorInputServiceImpl.saveInput() pour éviter les doublons
     * lors du flush de SensorDataPersistenceJob.
     */
    void removeFromBuffer(SensorInput input);
 
    /** Vider tout le buffer et retourner toutes les valeurs à persister */
    List<SensorInput> drainBuffer();
 
    /** Nombre d'entrées actuellement dans le buffer */
    int getBufferSize();
}
