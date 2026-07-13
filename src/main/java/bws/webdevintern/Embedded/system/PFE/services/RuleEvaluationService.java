package bws.webdevintern.Embedded.system.PFE.services;

import bws.webdevintern.Embedded.system.PFE.models.SensorInput;

public interface RuleEvaluationService {
	
	  /**
     * Évalue les règles actives du capteur et crée les alertes si nécessaire.
     *
     * IMPORTANT : cette méthode doit être appelée APRÈS inputRepository.save()
     * car elle crée des alertes qui référencent le SensorInput par son id MySQL.
     * Si input.getId() est null (pas encore sauvegardé), une erreur se produit.
     *
     * Cette méthode suppose que la quality est déjà calculée par calculerQuality().
     * Si quality = INVALID → aucune règle n'est évaluée.
     */
    void evaluate(SensorInput input);
 
    /**
     * Calcule la qualité de la mesure EN MÉMOIRE sans toucher à la base de données.
     *
     * Cette méthode doit être appelée AVANT inputRepository.save() pour que
     * le INSERT MySQL contienne directement la bonne valeur de quality.
     * Ainsi on évite un INSERT puis un UPDATE séparé.
     *
     * Résultat :
     *   - Si la valeur est hors plage (minRange / maxRange) → quality = INVALID
     *   - Si la valeur est dans la plage → quality reste null (valide)
     *   - Si aucune configuration active n'est trouvée → quality reste null
     */
    void calculerQuality(SensorInput input);

}
