package pharmacie.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pharmacie.entity.Ligne;

// Cette interface sera auto-implémentée par Spring
public interface LigneRepository extends JpaRepository<Ligne, Integer> {
    /**
     * Trouve toutes les lignes d'une commande donnée
     * @param commandoId l'identifiant de la commande
     * @return la liste des lignes de cette commande
     */
    List<Ligne> findByCommandeNumero(Integer commandoId);

    /**
     * Trouve toutes les lignes pour un médicament donné
     * @param medicamentId l'identifiant du médicament
     * @return la liste des lignes contenant ce médicament
     */
    List<Ligne> findByMedicamentReference(Integer medicamentId);

    /**
     * Calcule le nombre total d'articles commandés par un dispensaire
     * pour les commandes déjà envoyées (envoyele IS NOT NULL)
     * @param dispensaireId l'identifiant du dispensaire
     * @return le nombre total d'articles commandés et envoyés
     */
    @Query("SELECT SUM(l.quantite) FROM Ligne l " +
           "WHERE l.commande.dispensaire.id = :dispensaireId " +
           "AND l.commande.envoyele IS NOT NULL")
    Integer countArticlesCommandesByDispensaire(@Param("dispensaireId") Integer dispensaireId);
}
