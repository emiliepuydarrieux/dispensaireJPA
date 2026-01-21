package pharmacie.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pharmacie.entity.Medicament;

// Cette interface sera auto-implémentée par Spring
public interface MedicamentRepository extends JpaRepository<Medicament, Integer> {
    /**
     * Trouve un médicament à partir de son nom (unique dans Medicament)
     * @return un médicament "optionnel"
     */
    Optional<Medicament>findByNom(String nom);

    /**
     * Trouve les médicaments disponibles (indisponible = false)
     * @return la liste des médicaments disponibles
     */
    List<Medicament> findByIndisponibleFalse();

    /**
     * Trouve tous les médicaments disponibles à la commande pour une catégorie donnée
     * Un médicament est disponible à la commande si:
     * - il n'est pas indisponible (indisponible = false)
     * - sa quantité en stock (unitesEnStock) >= sa quantité en commande (unitesCommandees)
     * @param categorieCode le code de la catégorie
     * @return la liste des médicaments disponibles à la commande
     */
    @Query("SELECT m FROM Medicament m WHERE m.categorie.code = :categorieCode " +
           "AND m.indisponible = false " +
           "AND m.unitesEnStock >= m.unitesCommandees")
    List<Medicament> findMedicamentsDisponiblesALaCommande(@Param("categorieCode") Integer categorieCode);

    /**
     * Calcule le nombre d'unités commandées pour chaque médicament d'une catégorie
     * (diapositive 51 du support de cours)
     * @param codeCategorie la catégorie à traiter
     * @return le nombre d'unités commandées pour chaque médicament, 
     *         sous la forme d'une liste de projections UnitesParMedicament
     */
    @Query("SELECT ligne.medicament.nom as nom, SUM(ligne.quantite) AS unites " +
           "FROM Ligne ligne " +
           "WHERE ligne.medicament.categorie.code = :codeCategorie " +
           "GROUP BY ligne.medicament.nom")
    List<UnitesParMedicament> medicamentsVendusPour(@Param("codeCategorie") Integer codeCategorie);
}

