package pharmacie.dao;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pharmacie.entity.Categorie;
import pharmacie.entity.Commande;
import pharmacie.entity.Dispensaire;
import pharmacie.entity.Ligne;
import pharmacie.entity.Medicament;

@DataJpaTest
public class IntegrityConstraintsAndQueriesTest {

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MedicamentRepository medicamentRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private DispensaireRepository dispensaireRepository;
    @Autowired
    private LigneRepository ligneRepository;

    private Dispensaire testDispensaire;
    private Categorie testCategorie;

    @BeforeEach
    public void setUp() {
        // Créer un dispensaire de test
        testDispensaire = createTestDispensaire();
        dispensaireRepository.save(testDispensaire);

        // Créer une catégorie de test
        testCategorie = createTestCategorie();
        categorieRepository.save(testCategorie);
    }



    // ==================== REQUÊTES PERSONNALISÉES ====================

    @Test
    public void testFindCommandesEnCoursByDispensaire() {
        // Créer une commande EN COURS (envoyele = NULL)
        Commande cmdEnCours = createTestCommande(testDispensaire);
        cmdEnCours.setEnvoyele(null);
        commandeRepository.save(cmdEnCours);

        // Créer une commande ENVOYÉE (envoyele = date)
        Commande cmdEnvoyee = createTestCommande(testDispensaire);
        cmdEnvoyee.setEnvoyele(new Date());
        commandeRepository.save(cmdEnvoyee);

        // Trouver les commandes en cours
        List<Commande> commandesEnCours = commandeRepository.findCommandesEnCoursByDispensaire(testDispensaire.getId());

        assertEquals(1, commandesEnCours.size());
        assertNull(commandesEnCours.get(0).getEnvoyele());
    }

    @Test
    public void testCountArticlesCommandesByDispensaire() {
        // Créer un médicament
        Medicament med = createTestMedicament(testCategorie);
        medicamentRepository.save(med);

        // Commande ENVOYÉE
        Commande cmdEnvoyee = createTestCommande(testDispensaire);
        cmdEnvoyee.setEnvoyele(new Date());
        commandeRepository.save(cmdEnvoyee);

        Ligne ligne1 = new Ligne();
        ligne1.setMedicament(med);
        ligne1.setCommande(cmdEnvoyee);
        ligne1.setQuantite(10);
        ligneRepository.save(ligne1);

        Ligne ligne2 = new Ligne();
        ligne2.setMedicament(med);
        ligne2.setCommande(cmdEnvoyee);
        ligne2.setQuantite(5);
        ligneRepository.save(ligne2);

        // Commande EN COURS
        Commande cmdEnCours = createTestCommande(testDispensaire);
        cmdEnCours.setEnvoyele(null);
        commandeRepository.save(cmdEnCours);

        Ligne ligne3 = new Ligne();
        ligne3.setMedicament(med);
        ligne3.setCommande(cmdEnCours);
        ligne3.setQuantite(20); // Ne doit pas être compté
        ligneRepository.save(ligne3);

        // Compter les articles envoyés
        Integer totalArticles = ligneRepository.countArticlesCommandesByDispensaire(testDispensaire.getId());

        // Doit être 15 (10 + 5), la commande en cours ne compte pas
        assertEquals(15, totalArticles);
    }

    @Test
    public void testFindMedicamentsDisponiblesALaCommande() {
        // Médicament 1: disponible (stock 100 >= commande 30)
        Medicament med1 = new Medicament();
        med1.setNom("Med Disponible 1");
        med1.setCategorie(testCategorie);
        med1.setIndisponible(false);
        med1.setUnitesEnStock(100);
        med1.setUnitesCommandees(30);
        medicamentRepository.save(med1);

        // Médicament 2: indisponible (indisponible = true)
        Medicament med2 = new Medicament();
        med2.setNom("Med Indisponible");
        med2.setCategorie(testCategorie);
        med2.setIndisponible(true);
        med2.setUnitesEnStock(100);
        med2.setUnitesCommandees(30);
        medicamentRepository.save(med2);

        // Médicament 3: stock insuffisant (stock 20 < commande 50)
        Medicament med3 = new Medicament();
        med3.setNom("Med Stock Insuffisant");
        med3.setCategorie(testCategorie);
        med3.setIndisponible(false);
        med3.setUnitesEnStock(20);
        med3.setUnitesCommandees(50);
        medicamentRepository.save(med3);

        // Médicament 4: disponible (stock 50 >= commande 50)
        Medicament med4 = new Medicament();
        med4.setNom("Med Disponible 2");
        med4.setCategorie(testCategorie);
        med4.setIndisponible(false);
        med4.setUnitesEnStock(50);
        med4.setUnitesCommandees(50);
        medicamentRepository.save(med4);

        // Trouver les médicaments disponibles à la commande
        List<Medicament> medsDisponibles = medicamentRepository.findMedicamentsDisponiblesALaCommande(testCategorie.getCode());

        assertEquals(2, medsDisponibles.size());
        assertTrue(medsDisponibles.stream().anyMatch(m -> m.getNom().equals("Med Disponible 1")));
        assertTrue(medsDisponibles.stream().anyMatch(m -> m.getNom().equals("Med Disponible 2")));
        assertFalse(medsDisponibles.stream().anyMatch(m -> m.getNom().equals("Med Indisponible")));
        assertFalse(medsDisponibles.stream().anyMatch(m -> m.getNom().equals("Med Stock Insuffisant")));
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    private Dispensaire createTestDispensaire() {
        Dispensaire disp = new Dispensaire();
        disp.setCode("D_TEST");
        disp.setNom("Dispensaire Test");
        disp.setAdresse("123 Test Street");
        disp.setVille("TestCity");
        disp.setCode_postal("00000");
        disp.setPays("TestCountry");
        disp.setRegion("TestRegion");
        disp.setFax("01-00-00-00");
        disp.setTelephone("01-00-00-01");
        disp.setContact("Test Contact");
        disp.setFonction("Test Fonction");
        return disp;
    }

    private Commande createTestCommande(Dispensaire disp) {
        Commande cmd = new Commande();
        cmd.setDispensaire(disp);
        cmd.setSaisiele(new Date());
        cmd.setPort(0);
        cmd.setRemise(0);
        cmd.setCode_postal("75000");
        cmd.setPays("France");
        cmd.setVille("Paris");
        cmd.setAdresse("456 Test Avenue");
        cmd.setRegion("IDF");
        cmd.setDestinataire("Test Dest");
        return cmd;
    }

    private Categorie createTestCategorie() {
        Categorie cat = new Categorie();
        cat.setLibelle("TestCategory");
        cat.setDescription("Catégorie de test");
        return cat;
    }

    private Medicament createTestMedicament(Categorie cat) {
        Medicament med = new Medicament();
        med.setNom("Med Test");
        med.setCategorie(cat);
        med.setIndisponible(false);
        med.setUnitesEnStock(100);
        med.setUnitesCommandees(10);
        return med;
    }
}
