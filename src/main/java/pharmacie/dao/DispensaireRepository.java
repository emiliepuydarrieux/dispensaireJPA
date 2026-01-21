package pharmacie.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pharmacie.entity.Dispensaire;

// Cette interface sera auto-implémentée par Spring
public interface DispensaireRepository extends JpaRepository<Dispensaire, Integer> {
    /**
     * Trouve tous les dispensaires d'une région donnée
     * @param region la région recherchée
     * @return la liste des dispensaires dans cette région
     */
    List<Dispensaire> findByRegion(String region);
}
