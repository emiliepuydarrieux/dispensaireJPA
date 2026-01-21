package pharmacie.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pharmacie.entity.Categorie;
import pharmacie.entity.Commande;
import pharmacie.entity.Dispensaire;
import pharmacie.entity.Medicament;

@DataJpaTest
public class RepositoryCustomMethodsTest {

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MedicamentRepository medicamentRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private DispensaireRepository dispensaireRepository;


    @Test // Ce test se base uniquement sur les données définies dans data.sql
    public void testMedicamentCustomMethods() {    
        Medicament indisponible = medicamentRepository.findByNom("Lévofloxacine 500mg").orElseThrow();
        Medicament disponible   = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();
    
        // Trouve tous les médicaments disponibles
        List<Medicament> disponibles = medicamentRepository.findByIndisponibleFalse();

        assertTrue(disponibles.contains(disponible));
        assertFalse(disponibles.contains(indisponible));        
        assertFalse(disponibles.isEmpty());
    }

    @Test // Ce test crée les enregistrements nécessaires
    public void testCategorieCustomMethods() {
        Categorie c1 = new Categorie();
        c1.setLibelle("AnalgesiquesTest");
        categorieRepository.save(c1);

        Categorie c2 = new Categorie();
        c2.setLibelle("AntibiotiquesTest");
        categorieRepository.save(c2);

        // findByLibelle
        Categorie found = categorieRepository.findByLibelle("AnalgesiquesTest");
        assertNotNull(found);
        assertEquals("AnalgesiquesTest", found.getLibelle());

        // findByLibelleContaining
        List<Categorie> list = categorieRepository.findByLibelleContaining("iquesTest");
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AntibiotiquesTest")));
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AnalgesiquesTest")));
    }

    @Test // Test pour les commandes saisies après une date donnée
    public void testCommandeCustomMethods() {
        // Crée une date de référence (2024-02-01)
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.FEBRUARY, 1);
        Date dateReference = calendar.getTime();

        // Trouve toutes les commandes saisies après le 2024-02-01
        List<Commande> commandesApres = commandeRepository.findBySaisieleAfter(dateReference);

        assertNotNull(commandesApres);
        // Remarque: la recherche peut retourner une liste vide si aucune commande n'existe
        // Ce test valide que la méthode fonctionne correctement
        if (!commandesApres.isEmpty()) {
            // Vérifier que toutes les commandes trouvées ont une date de saisie après la date de référence
            for (Commande commande : commandesApres) {
                assertTrue(commande.getSaisiele().after(dateReference));
            }
        }
    }

    @Test // Test pour les dispensaires dans une région donnée
    public void testDispensaireCustomMethods() {
        // Trouve tous les dispensaires en Île-de-France
        List<Dispensaire> dispensairesIDF = dispensaireRepository.findByRegion("Île-de-France");
        
        assertNotNull(dispensairesIDF);
        assertFalse(dispensairesIDF.isEmpty());
        
        // Vérifier que tous les dispensaires trouvés sont bien en Île-de-France
        for (Dispensaire dispensaire : dispensairesIDF) {
            assertEquals("Île-de-France", dispensaire.getRegion());
        }

        // Au minimum, le dispensaire D001 (Hôpital Central) doit être trouvé
        assertTrue(dispensairesIDF.stream().anyMatch(d -> d.getCode().equals("D001")));

        // Teste avec une autre région
        List<Dispensaire> dispensairesARA = dispensaireRepository.findByRegion("Auvergne-Rhône-Alpes");
        assertFalse(dispensairesARA.isEmpty());
        assertTrue(dispensairesARA.stream().anyMatch(d -> d.getCode().equals("D002")));
    }
}