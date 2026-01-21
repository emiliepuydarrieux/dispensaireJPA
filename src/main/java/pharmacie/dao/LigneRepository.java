package pharmacie.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
