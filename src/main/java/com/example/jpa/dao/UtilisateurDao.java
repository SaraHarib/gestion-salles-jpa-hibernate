package com.example.jpa.dao;

import com.example.jpa.entities.Utilisateur;
import java.util.List;
import javax.persistence.EntityManager;

public class UtilisateurDao extends GenericDao<Utilisateur> {

    public UtilisateurDao(EntityManager gestionnaireEntites) {
        super(gestionnaireEntites, Utilisateur.class);
    }

    public List<Utilisateur> findBySalleNom(String nomSalleRecherchee) {
        String requeteJpql = "SELECT utilisateurRecherche FROM Utilisateur utilisateurRecherche "
                + "WHERE utilisateurRecherche.salle.nom = :nomSalleRecherchee";
        return gestionnaireEntites.createQuery(requeteJpql, Utilisateur.class)
                .setParameter("nomSalleRecherchee", nomSalleRecherchee)
                .getResultList();
    }
}
