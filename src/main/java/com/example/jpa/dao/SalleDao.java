package com.example.jpa.dao;

import com.example.jpa.entities.Salle;
import java.util.List;
import javax.persistence.EntityManager;

public class SalleDao extends GenericDao<Salle> {

    public SalleDao(EntityManager gestionnaireEntites) {
        super(gestionnaireEntites, Salle.class);
    }

    public List<Salle> findByCapaciteMin(int capaciteMinimum) {
        String requeteJpql = "SELECT salleRecherchee FROM Salle salleRecherchee "
                + "WHERE salleRecherchee.capacite >= :capaciteMinimum";
        return gestionnaireEntites.createQuery(requeteJpql, Salle.class)
                .setParameter("capaciteMinimum", capaciteMinimum)
                .getResultList();
    }
}
