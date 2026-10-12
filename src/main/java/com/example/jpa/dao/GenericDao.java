package com.example.jpa.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class GenericDao<T> {

    protected final EntityManager gestionnaireEntites;
    private final Class<T> classeEntite;

    public GenericDao(EntityManager gestionnaireEntites, Class<T> classeEntite) {
        this.gestionnaireEntites = gestionnaireEntites;
        this.classeEntite = classeEntite;
    }

    public void create(T entiteACreer) {
        EntityTransaction transactionLocale = gestionnaireEntites.getTransaction();
        try {
            transactionLocale.begin();
            gestionnaireEntites.persist(entiteACreer);
            transactionLocale.commit();
        } catch (RuntimeException exception) {
            annulerTransaction(transactionLocale, exception);
            throw exception;
        }
    }

    public T find(Long identifiant) {
        return gestionnaireEntites.find(classeEntite, identifiant);
    }

    public T update(T entiteAModifier) {
        EntityTransaction transactionLocale = gestionnaireEntites.getTransaction();
        try {
            transactionLocale.begin();
            // merge renvoie l'instance gérée, qui peut différer de celle reçue.
            T entiteMiseAJour = gestionnaireEntites.merge(entiteAModifier);
            transactionLocale.commit();
            return entiteMiseAJour;
        } catch (RuntimeException exception) {
            annulerTransaction(transactionLocale, exception);
            throw exception;
        }
    }

    public void delete(T entiteASupprimer) {
        EntityTransaction transactionLocale = gestionnaireEntites.getTransaction();
        try {
            transactionLocale.begin();
            // remove attend une entité rattachée au gestionnaire courant.
            T entitePersistante = gestionnaireEntites.contains(entiteASupprimer)
                    ? entiteASupprimer : gestionnaireEntites.merge(entiteASupprimer);
            gestionnaireEntites.remove(entitePersistante);
            transactionLocale.commit();
        } catch (RuntimeException exception) {
            annulerTransaction(transactionLocale, exception);
            throw exception;
        }
    }

    public List<T> findAll() {
        // JPQL utilise le nom de l'entité, pas celui de la table SQL.
        String nomEntite = gestionnaireEntites.getMetamodel().entity(classeEntite).getName();
        String requeteJpql = "SELECT entite FROM " + nomEntite + " entite";
        return gestionnaireEntites.createQuery(requeteJpql, classeEntite).getResultList();
    }

    private void annulerTransaction(EntityTransaction transactionLocale, RuntimeException exceptionInitiale) {
        try {
            if (transactionLocale.isActive()) {
                transactionLocale.rollback();
            }
        } catch (RuntimeException exceptionRollback) {
            // On conserve l'erreur initiale même si le rollback échoue.
            exceptionInitiale.addSuppressed(exceptionRollback);
        }
    }
}
