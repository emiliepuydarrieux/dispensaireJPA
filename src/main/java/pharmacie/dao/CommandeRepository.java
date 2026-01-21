package pharmacie.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pharmacie.entity.Commande;

// Cette interface sera auto-implémentée par Spring
public interface CommandeRepository extends JpaRepository<Commande, Integer> {
    /**
     * Trouve toutes les commandes saisies après une date donnée
     * @param date la date limite
     * @return la liste des commandes saisies après cette date
     */
    List<Commande> findBySaisieleAfter(Date date);
}
