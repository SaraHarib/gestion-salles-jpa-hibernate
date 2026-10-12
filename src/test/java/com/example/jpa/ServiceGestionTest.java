package com.example.jpa;

import com.example.jpa.dao.SalleDao;
import com.example.jpa.entities.Salle;
import com.example.jpa.entities.Utilisateur;
import com.example.jpa.service.ServiceGestion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServiceGestionTest {

    private ServiceGestion serviceGestion;
    private EntityManagerFactory factoryVerification;
    private EntityManager gestionnaireVerification;

    @Before
    public void preparerChaqueTest() {
        // Ce contexte distinct permet de relire les données réellement enregistrées.
        factoryVerification = Persistence.createEntityManagerFactory("gestionPU");
        gestionnaireVerification = factoryVerification.createEntityManager();
        nettoyerBase();
        serviceGestion = new ServiceGestion();
    }

    @After
    public void fermerLesRessourcesApresChaqueTest() {
        // Les ressources sont fermées même si une assertion ou le nettoyage échoue.
        try {
            if (serviceGestion != null) {
                serviceGestion.close();
            }
        } finally {
            try {
                if (gestionnaireVerification != null && gestionnaireVerification.isOpen()) {
                    try {
                        nettoyerBase();
                    } finally {
                        gestionnaireVerification.close();
                    }
                }
            } finally {
                if (factoryVerification != null && factoryVerification.isOpen()) {
                    factoryVerification.close();
                }
            }
        }
    }

    @Test
    public void creerUneSalleEtRelireSesInformations() {
        Salle salleAtlas = serviceGestion.ajouterSalle("Salle Atlas", 32);

        assertNotNull(salleAtlas.getId());
        Salle salleRelue = gestionnaireVerification.find(Salle.class, salleAtlas.getId());
        assertNotNull(salleRelue);
        assertEquals("Salle Atlas", salleRelue.getNom());
        assertEquals(32, salleRelue.getCapacite());

        List<Salle> listeSalles = serviceGestion.listerSalles();
        assertEquals(1, listeSalles.size());
        assertEquals(salleAtlas.getId(), listeSalles.get(0).getId());
    }

    @Test
    public void creerUnUtilisateurEtConserverSonAssociationAvecLaSalle() {
        Salle salleOasis = serviceGestion.ajouterSalle("Salle Oasis", 18);
        Utilisateur utilisateurCree = serviceGestion.ajouterUtilisateur(
                "Ines Haddad", "ines.haddad@example.com", salleOasis);

        assertNotNull(utilisateurCree.getId());
        Utilisateur utilisateurRelu = gestionnaireVerification.find(
                Utilisateur.class, utilisateurCree.getId());
        assertNotNull(utilisateurRelu);
        assertEquals("Ines Haddad", utilisateurRelu.getNom());
        assertEquals("ines.haddad@example.com", utilisateurRelu.getEmail());
        assertNotNull(utilisateurRelu.getSalle());
        assertEquals(salleOasis.getId(), utilisateurRelu.getSalle().getId());
        assertEquals("Salle Oasis", utilisateurRelu.getSalle().getNom());

        List<Utilisateur> listeUtilisateurs = serviceGestion.listerUtilisateurs();
        assertEquals(1, listeUtilisateurs.size());
        assertEquals(utilisateurCree.getId(), listeUtilisateurs.get(0).getId());
    }

    @Test
    public void rechercherUniquementLesUtilisateursDeLaSalleDemandee() {
        Salle salleMajorelle = serviceGestion.ajouterSalle("Salle Majorelle", 24);
        Salle salleOasis = serviceGestion.ajouterSalle("Salle Oasis", 16);
        Utilisateur premierUtilisateur = serviceGestion.ajouterUtilisateur(
                "Nadir Mansouri", "nadir.mansouri@example.com", salleMajorelle);
        Utilisateur secondUtilisateur = serviceGestion.ajouterUtilisateur(
                "Salma Idrissi", "salma.idrissi@example.com", salleMajorelle);
        serviceGestion.ajouterUtilisateur("Yanis Karim", "yanis.karim@example.com", salleOasis);

        List<Utilisateur> utilisateursTrouves = serviceGestion.rechercherUtilisateursParSalle("Salle Majorelle");
        assertEquals(2, utilisateursTrouves.size());
        List<Long> identifiantsTrouves = utilisateursTrouves.stream()
                .map(Utilisateur::getId)
                .toList();
        assertTrue(identifiantsTrouves.contains(premierUtilisateur.getId()));
        assertTrue(identifiantsTrouves.contains(secondUtilisateur.getId()));
        assertTrue(serviceGestion.rechercherUtilisateursParSalle("Salle Inconnue").isEmpty());
    }

    @Test
    public void rechercherLesSallesDontLaCapaciteAtteintLeMinimum() {
        serviceGestion.ajouterSalle("Salle Oasis", 19);
        Salle salleAtlas = serviceGestion.ajouterSalle("Salle Atlas", 20);
        Salle salleMajorelle = serviceGestion.ajouterSalle("Salle Majorelle", 45);
        SalleDao daoSalles = new SalleDao(gestionnaireVerification);

        // La salle de capacité exactement égale au seuil doit être retenue.
        List<Salle> sallesDisponibles = daoSalles.findByCapaciteMin(20);
        assertEquals(2, sallesDisponibles.size());
        List<Long> identifiantsTrouves = sallesDisponibles.stream()
                .map(Salle::getId)
                .toList();
        assertTrue(identifiantsTrouves.contains(salleAtlas.getId()));
        assertTrue(identifiantsTrouves.contains(salleMajorelle.getId()));
        assertTrue(daoSalles.findByCapaciteMin(46).isEmpty());
    }

    private void nettoyerBase() {
        EntityTransaction transactionLocale = gestionnaireVerification.getTransaction();
        try {
            transactionLocale.begin();
            // H2 garde les données entre les tests ; les utilisateurs sont supprimés en premier.
            gestionnaireVerification.createQuery("DELETE FROM Utilisateur").executeUpdate();
            gestionnaireVerification.createQuery("DELETE FROM Salle").executeUpdate();
            transactionLocale.commit();
            gestionnaireVerification.clear();
        } catch (RuntimeException exception) {
            if (transactionLocale.isActive()) {
                transactionLocale.rollback();
            }
            throw exception;
        }
    }
}
