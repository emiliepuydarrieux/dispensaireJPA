package pharmacie.dao;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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



    // ==================== CONTRAINTES D'INTÉGRITÉ ====================

    @Test
    public void testMedicamentRequiresCategory() {
        // Un médicament doit avoir une catégorie (diapositive 64)
        Medicament med = new Medicament();
        med.setNom("Med sans catégorie");
        // Pas de setCategorie() - doit causer une erreur
        
        // Cette assertion devrait échouer car categorie est @NonNull
        try {
            medicamentRepository.save(med);
            // Si on arrive ici, c'est qu'il n'y a pas de vérification
            assertTrue(false, "Le medicament aurait dû avoir besoin d'une catégorie");
        } catch (Exception e) {
            // C'est attendu - la validation ou la BD rejette l'insertion
            assertTrue(true);
        }
    }

    @Test
    public void testCanDeleteEmptyCategory() {
        // On peut supprimer une catégorie qui n'a pas de médicaments
        Categorie emptyCategory = new Categorie();
        emptyCategory.setLibelle("Catégorie Vide");
        categorieRepository.save(emptyCategory);
        Integer categoryId = emptyCategory.getCode();

        // Supprimer la catégorie
        categorieRepository.deleteById(categoryId);

        // Vérifier qu'elle est bien supprimée
        assertTrue(categorieRepository.findById(categoryId).isEmpty());
    }

    @Test
    public void testCannotDeleteCategoryWithMedicaments() {
        // On ne peut pas supprimer une catégorie qui a des médicaments
        Categorie catWithMeds = new Categorie();
        catWithMeds.setLibelle("Catégorie avec médicaments");
        categorieRepository.save(catWithMeds);

        // Ajouter un médicament à cette catégorie
        Medicament med = new Medicament();
        med.setNom("Med dans catégorie");
        med.setCategorie(catWithMeds);
        med.setIndisponible(false);
        med.setUnitesEnStock(50);
        med.setUnitesCommandees(10);
        medicamentRepository.save(med);

        // Essayer de supprimer la catégorie - doit échouer
        try {
            categorieRepository.deleteById(catWithMeds.getCode());
            // Forcer un flush pour voir la contrainte
            categorieRepository.flush();
            assertTrue(false, "La catégorie aurait dû ne pas pouvoir être supprimée");
        } catch (Exception e) {
            // C'est attendu - la BD rejette la suppression
            assertTrue(true);
        }
    }

    @Test
    public void testDeleteCommandeDeleteLines() {
        // Quand on supprime une commande, on supprime ses lignes (cascade)
        Commande cmd = createTestCommande(testDispensaire);
        commandeRepository.save(cmd);

        Medicament med = createTestMedicament(testCategorie);
        medicamentRepository.save(med);

        // Ajouter des lignes à la commande
        Ligne ligne1 = new Ligne();
        ligne1.setCommande(cmd);
        ligne1.setMedicament(med);
        ligne1.setQuantite(10);
        ligneRepository.save(ligne1);

        Ligne ligne2 = new Ligne();
        ligne2.setCommande(cmd);
        ligne2.setMedicament(med);
        ligne2.setQuantite(20);
        ligneRepository.save(ligne2);

        Integer commandeId = cmd.getNumero();
        long initialLineCount = ligneRepository.count();
        assertEquals(2, initialLineCount, "Doit avoir 2 lignes");

        // Supprimer la commande
        commandeRepository.deleteById(commandeId);

        // Vérifier que les lignes ont aussi été supprimées (cascade)
        long finalLineCount = ligneRepository.count();
        assertEquals(0, finalLineCount, "Les lignes auraient dû être supprimées");
    }

    @Test
    public void testDeleteDispensaireDeleteCommandes() {
        // Quand on supprime un dispensaire, on supprime ses commandes (cascade)
        Commande cmd1 = createTestCommande(testDispensaire);
        commandeRepository.save(cmd1);

        Commande cmd2 = createTestCommande(testDispensaire);
        commandeRepository.save(cmd2);

        Integer dispensaireId = testDispensaire.getId();
        List<Commande> initialCommandes = commandeRepository.findAll();
        int initialSize = initialCommandes.size();
        assertTrue(initialSize >= 2, "Doit avoir au moins 2 commandes");

        // Supprimer le dispensaire
        dispensaireRepository.deleteById(dispensaireId);

        // Vérifier que les commandes ont aussi été supprimées (cascade)
        List<Commande> finalCommandes = commandeRepository.findAll();
        int finalSize = finalCommandes.size();
        assertEquals(initialSize - 2, finalSize, "Les commandes auraient dû être supprimées");
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

    @Test
    public void testMedicamentsVendusPour() {
        // Créer plusieurs médicaments dans la même catégorie
        Medicament med1 = new Medicament();
        med1.setNom("Paracétamol");
        med1.setCategorie(testCategorie);
        med1.setIndisponible(false);
        medicamentRepository.save(med1);

        Medicament med2 = new Medicament();
        med2.setNom("Ibuprofène");
        med2.setCategorie(testCategorie);
        med2.setIndisponible(false);
        medicamentRepository.save(med2);

        // Créer une commande avec des lignes
        Commande cmd = createTestCommande(testDispensaire);
        commandeRepository.save(cmd);

        Ligne ligne1 = new Ligne();
        ligne1.setCommande(cmd);
        ligne1.setMedicament(med1);
        ligne1.setQuantite(50);
        ligneRepository.save(ligne1);

        Ligne ligne2 = new Ligne();
        ligne2.setCommande(cmd);
        ligne2.setMedicament(med1); // Même médicament
        ligne2.setQuantite(30);
        ligneRepository.save(ligne2);

        Ligne ligne3 = new Ligne();
        ligne3.setCommande(cmd);
        ligne3.setMedicament(med2);
        ligne3.setQuantite(20);
        ligneRepository.save(ligne3);

        // Récupérer les unités vendues par médicament (projection)
        List<UnitesParMedicament> results = medicamentRepository.medicamentsVendusPour(testCategorie.getCode());

        assertFalse(results.isEmpty(), "Il doit avoir des résultats");
        
        // Vérifier que les totaux sont corrects
        UnitesParMedicament paracetamol = results.stream()
            .filter(r -> r.getNom().equals("Paracétamol"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(paracetamol, "Paracétamol doit être dans les résultats");
        assertEquals(80L, paracetamol.getUnites(), "Paracétamol doit avoir 80 unités (50+30)");
    }    // ==================== MÉTHODES UTILITAIRES ====================

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
