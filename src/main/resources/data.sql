-- Données de base pour le projet Pharmacie
-- Dispensaire (Etablissements de santé qui passent commande de médicaments)
-- Le fichier est chargé au démarrage de l''application

-- Insertion des catégories de médicaments
INSERT INTO CATEGORIE (CODE, LIBELLE, DESCRIPTION) VALUES
(DEFAULT, 'Antalgiques et Antipyrétiques', 'Médicaments contre la douleur et la fièvre'), -- code : 1
(DEFAULT, 'Anti-inflammatoires', 'Médicaments réduisant l''inflammation'), -- code : 2
(DEFAULT, 'Antibiotiques', 'Médicaments pour traiter les infections bactériennes'),
(DEFAULT, 'Antihypertenseurs', 'Médicaments pour traiter l''hypertension artérielle'),
(DEFAULT, 'Antidiabétiques', 'Médicaments pour traiter le diabète'),
(DEFAULT, 'Antihistaminiques', 'Médicaments pour traiter les allergies'),
(DEFAULT, 'Vitamines et Compléments', 'Suppléments nutritionnels'),
(DEFAULT, 'Médicaments Cardiovasculaires', 'Médicaments pour le cœur et la circulation'),
(DEFAULT, 'Médicaments Gastro-intestinaux', 'Médicaments pour les troubles digestifs'),
(DEFAULT, 'Médicaments Respiratoires', 'Médicaments pour les troubles respiratoires');


-- Catégorie 1: Antalgiques et Antipyrétiques
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Morphine 10mg', 1, 'Boîte de 14 comprimés', 25.80, 80, 0, 15, false, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400'),
('Doliprane Effervescent 1g', 1, 'Boîte de 8 comprimés', 3.50, 280, 0, 30, false, 'https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=400'),
('Efferalgan Vitamine C', 1, 'Boîte de 16 comprimés', 4.20, 220, 0, 25, false, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=400');

-- Catégorie 2: Anti-inflammatoires
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Étodolac 400mg', 2, 'Boîte de 14 comprimés', 12.50, 110, 0, 15, false, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=400'),
('Flurbiprofène 100mg', 2, 'Boîte de 30 comprimés', 10.80, 130, 0, 16, false, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400');

-- Catégorie 3: Antibiotiques (2 médicaments indisponbibles)
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Lévofloxacine 500mg', 3, 'Boîte de 7 comprimés', 15.80, 160, 0, 18, true, 'https://images.unsplash.com/photo-1628771065518-0d82f1938462?w=400'),
('Clindamycine 300mg', 3, 'Boîte de 16 gélules', 13.20, 140, 0, 16, true, 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400');

-- Insertion des dispensaires
INSERT INTO DISPENSAIRE (ID, CODE, NOM, ADRESSE, VILLE, CODE_POSTAL, PAYS, REGION, FAX, TELEPHONE, CONTACT, FONCTION) VALUES
(DEFAULT, 'D001', 'Hôpital Central', '123 Avenue de la Paix', 'Paris', '75001', 'France', 'Île-de-France', '01-23-45-67', '01-23-45-68', 'Dr. Martin Dupont', 'Directeur'),
(DEFAULT, 'D002', 'Clinique Saint-Louis', '456 Rue de la Santé', 'Lyon', '69001', 'France', 'Auvergne-Rhône-Alpes', '04-56-78-90', '04-56-78-91', 'Dr. Sophie Bernard', 'Chef de Pharmacie'),
(DEFAULT, 'D003', 'Centre Médical de Provence', '789 Boulevard du Soleil', 'Marseille', '13001', 'France', 'Provence-Alpes-Côte d''Azur', '04-91-23-45', '04-91-23-46', 'Dr. Pierre Moreau', 'Pharmacien'),
(DEFAULT, 'D004', 'Hôpital de Bordeaux', '321 Chemin des Vignes', 'Bordeaux', '33000', 'France', 'Nouvelle-Aquitaine', '05-12-34-56', '05-12-34-57', 'Dr. Marie Laurent', 'Directrice'),
(DEFAULT, 'D005', 'Dispensaire du Nord', '654 Rue du Commerce', 'Lille', '59000', 'France', 'Hauts-de-France', '03-98-76-54', '03-98-76-55', 'Dr. Jean Durand', 'Responsable');

-- Insertion des commandes
-- Les dates sont en format DATE (YYYY-MM-DD)
-- (Pas d'insertion directe ici - utiliser l'application pour créer les commandes)

-- Insertion des lignes de commande
-- Chaque ligne associe un médicament à une commande avec une quantité
-- (Pas d'insertion directe ici - utiliser l'application pour créer les lignes)
