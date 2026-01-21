package pharmacie.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pharmacie.entity.Commande;

// Cette interface sera auto-implémentée par Spring
public interface CommandeRepository extends JpaRepository<Commande, Integer> {
    /**
     * Trouve toutes les commandes saisies après une date donnée
     * @param date la date limite
     * @return la liste des commandes saisies après cette date
     */
    List<Commande> findBySaisieleAfter(Date date);

    /**
     * Trouve toutes les commandes en cours pour un dispensaire donné
     * Une commande est en cours si sa date d'envoi (envoyele) n'est pas renseignée
     * @param dispensaireId l'identifiant du dispensaire
     * @return la liste des commandes en cours
     */
    @Query("SELECT c FROM Commande c WHERE c.dispensaire.id = :dispensaireId AND c.envoyele IS NULL")
    List<Commande> findCommandesEnCoursByDispensaire(@Param("dispensaireId") Integer dispensaireId);

    /**
     * Trouve toutes les commandes déjà envoyées pour un dispensaire donné
     * @param dispensaireId l'identifiant du dispensaire
     * @return la liste des commandes envoyées
     */
    @Query("SELECT c FROM Commande c WHERE c.dispensaire.id = :dispensaireId AND c.envoyele IS NOT NULL")
    List<Commande> findCommandesEnvoyeesByDispensaire(@Param("dispensaireId") Integer dispensaireId);
}
