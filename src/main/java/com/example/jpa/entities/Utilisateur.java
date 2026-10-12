package com.example.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    // Plusieurs utilisateurs peuvent partager la même salle.
    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    // Constructeur nécessaire à JPA.
    public Utilisateur() {
    }

    public Utilisateur(String nomUtilisateur, String emailUtilisateur, Salle salleAffectee) {
        this.nom = nomUtilisateur;
        this.email = emailUtilisateur;
        this.salle = salleAffectee;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nomUtilisateur) {
        this.nom = nomUtilisateur;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String emailUtilisateur) {
        this.email = emailUtilisateur;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salleAffectee) {
        this.salle = salleAffectee;
    }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", nom='" + nom + "', email='" + email
                + "', salleId=" + (salle != null ? salle.getId() : null) + "}";
    }
}
