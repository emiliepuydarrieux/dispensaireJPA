package pharmacie.dao;

/**
 * Projection pour les résultats de la requête medicamentsVendusPour
 * Permet de récupérer le nombre d'unités commandées pour chaque médicament
 */
public interface UnitesParMedicament {
    /**
     * @return le nom du médicament
     */
    String getNom();

    /**
     * @return le nombre total d'unités commandées pour ce médicament
     */
    Long getUnites();
}
