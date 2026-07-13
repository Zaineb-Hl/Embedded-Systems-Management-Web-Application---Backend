package bws.webdevintern.Embedded.system.PFE.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
import bws.webdevintern.Embedded.system.PFE.repositories.SensorInputRepository;
import bws.webdevintern.Embedded.system.PFE.services.SensorCacheService;

@Component
@EnableScheduling
public class SensorDataPersistenceJob {
	
    private static final Logger log = LoggerFactory.getLogger(SensorDataPersistenceJob.class);
    
    
    @Autowired
    private SensorCacheService sensorCacheService;

    @Autowired
    private SensorInputRepository sensorInputRepository;


    /**
     * Cette méthode est appelée automatiquement toutes les 30 secondes.
     *
     * Son rôle : récupérer toutes les mesures qui sont en attente dans
     * le buffer Hazelcast (parce que MySQL était indisponible au moment
     * de leur réception) et les enregistrer dans MySQL maintenant que
     * la base de données est peut-être de nouveau accessible.
     *
     * Si MySQL est toujours en panne, les mesures sont remises dans le
     * buffer pour réessayer au prochain cycle de 30 secondes.
     * Ainsi aucune mesure n'est jamais perdue.
     */
    @Scheduled(fixedDelay = 30_000) // toutes les 30 secondes
    public void flushBufferToDatabase() {
 
        // ── Étape 1 : Vérifier si le buffer contient des mesures ────────────
        // On regarde combien de mesures attendent dans le buffer Hazelcast.
        // Si le buffer est vide, cela signifie que MySQL était disponible
        // lors de tous les POST récents → rien à faire, on s'arrête ici.
        int nombreMesuresEnAttente = sensorCacheService.getBufferSize();
        if (nombreMesuresEnAttente == 0) {
            return; // buffer vide, pas besoin de faire quoi que ce soit
        }
 
        log.info("[Hazelcast → MySQL] Flush en cours : {} mesure(s) en attente dans le buffer",
                nombreMesuresEnAttente);
 
        // ── Étape 2 : Récupérer toutes les mesures du buffer ────────────────
        // drainBuffer() vide le buffer et retourne toutes les mesures qu'il contenait.
        // IMPORTANT : après cette ligne, le buffer est vide.
        // Si MySQL échoue, il faudra remettre les mesures dans le buffer manuellement
        // sinon elles seront perdues définitivement.
        List<SensorInput> mesuresAEnregistrer = sensorCacheService.drainBuffer();
 
        if (mesuresAEnregistrer.isEmpty()) {
            return; // sécurité supplémentaire : si la liste est vide on s'arrête
        }
 
        // ── Étape 3 : Préparer deux listes pour trier les résultats ─────────
        // mesuresReussies  → mesures qui ont été enregistrées dans MySQL avec succès
        // mesuresEchouees  → mesures pour lesquelles MySQL a encore échoué
        //                    (on les remettra dans le buffer à la fin)
        int mesuresReussies = 0;
        List<SensorInput> mesuresEchouees = new ArrayList<>();
 
        // ── Étape 4 : Essayer d'enregistrer chaque mesure une par une ───────
        // On traite les mesures une par une et non pas toutes en même temps.
        // Pourquoi ? Parce que si on utilisait saveAll() (tout en une fois) :
        //   → si MySQL échoue au milieu, TOUTES les mesures sont perdues
        // En traitant une par une :
        //   → si MySQL échoue pour une mesure, on la remet dans le buffer
        //   → les autres mesures continuent d'être traitées normalement
        for (SensorInput mesure : mesuresAEnregistrer) {
            try {
 
                // On tente d'enregistrer cette mesure dans MySQL
                sensorInputRepository.save(mesure);
                mesuresReussies++;
 
            } catch (Exception erreur) {
 
                // MySQL est toujours indisponible pour cette mesure →
                // on l'ajoute à la liste des échecs pour la remettre dans le buffer
                mesuresEchouees.add(mesure);
 
                log.warn("[Hazelcast → MySQL] Impossible d'enregistrer la mesure du capteur {} : {}",
                        mesure.getSensor() != null ? mesure.getSensor().getId() : "inconnu",
                        erreur.getMessage());
            }
        }
 
        // ── Étape 5 : Remettre les mesures échouées dans le buffer ──────────
        // Pour chaque mesure que MySQL n'a pas pu accepter, on la remet
        // dans le buffer Hazelcast. Elle sera réessayée dans 30 secondes.
        // Cela garantit qu'aucune mesure n'est jamais perdue, même en cas
        // de panne prolongée de MySQL (plusieurs heures par exemple).
        if (!mesuresEchouees.isEmpty()) {
            for (SensorInput mesureEchouee : mesuresEchouees) {
                sensorCacheService.addToBuffer(mesureEchouee);
            }
            log.warn("[Hazelcast → MySQL] {} mesure(s) remise(s) dans le buffer — "
                    + "MySQL toujours indisponible, prochain essai dans 30 secondes",
                    mesuresEchouees.size());
        }
 
        // ── Étape 6 : Afficher le résultat final dans la console ────────────
        // Ce message permet de savoir combien de mesures ont été sauvegardées
        // et combien sont encore en attente pour le prochain cycle.
        log.info("[Hazelcast → MySQL] Résultat : {} enregistrée(s) avec succès, "
                + "{} encore en attente dans le buffer",
                mesuresReussies, mesuresEchouees.size());
    }

}
