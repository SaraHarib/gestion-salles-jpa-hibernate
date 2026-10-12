package com.example.jpa.service;

import com.example.jpa.dao.SalleDao;
import com.example.jpa.dao.UtilisateurDao;
import com.example.jpa.entities.Salle;
import com.example.jpa.entities.Utilisateur;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

// Le service expose les opérations métier et délègue la persistance aux DAO.
public class ServiceGestion implements AutoCloseable {

    private final EntityManagerFactory factoryGestion;
    private final EntityManager gestionnaireEntites;
    private final SalleDao daoSalles;
    private final UtilisateurDao daoUtilisateurs;

    public ServiceGestion() {
        factoryGestion = Persistence.createEntityManagerFactory("gestionPU");
        try {
            gestionnaireEntites = factoryGestion.createEntityManager();
        } catch (RuntimeException exception) {
            // On libère la factory si l'ouverture du gestionnaire échoue.
            try {
                factoryGestion.close();
            } catch (RuntimeException exceptionFermeture) {
                exception.addSuppressed(exceptionFermeture);
            }
            throw exception;
        }
        daoSalles = new SalleDao(gestionnaireEntites);
        daoUtilisateurs = new UtilisateurDao(gestionnaireEntites);
    }

    public Salle ajouterSalle(String nomSalle, int capaciteSalle) {
        Salle salleCreee = new Salle(nomSalle, capaciteSalle);
        daoSalles.create(salleCreee);
        return salleCreee;
    }

    public Utilisateur ajouterUtilisateur(String nomUtilisateur, String emailUtilisateur, Salle salleAffectee) {
        Utilisateur utilisateurCree = new Utilisateur(nomUtilisateur, emailUtilisateur, salleAffectee);
        daoUtilisateurs.create(utilisateurCree);
        return utilisateurCree;
    }

    public List<Salle> listerSalles() {
        return daoSalles.findAll();
    }

    public List<Utilisateur> listerUtilisateurs() {
        return daoUtilisateurs.findAll();
    }

    public List<Salle> rechercherSallesParCapacite(int capaciteMinimum) {
        return daoSalles.findByCapaciteMin(capaciteMinimum);
    }

    public List<Utilisateur> rechercherUtilisateursParSalle(String nomSalleRecherchee) {
        return daoUtilisateurs.findBySalleNom(nomSalleRecherchee);
    }

    @Override
    public void close() {
        // Les finally assurent la fermeture du gestionnaire puis de la factory.
        try {
            if (gestionnaireEntites.isOpen()) {
                try {
                    EntityTransaction transactionLocale = gestionnaireEntites.getTransaction();
                    if (transactionLocale.isActive()) {
                        transactionLocale.rollback();
                    }
                } finally {
                    gestionnaireEntites.close();
                }
            }
        } finally {
            if (factoryGestion.isOpen()) {
                factoryGestion.close();
            }
        }
    }
}
