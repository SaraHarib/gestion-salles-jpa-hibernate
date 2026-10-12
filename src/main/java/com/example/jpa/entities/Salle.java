package com.example.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Salle {

    // La base attribue l'identifiant lors de l'insertion.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false)
    private int capacite;

    // JPA utilise ce constructeur pour recréer une salle depuis la base.
    public Salle() {
    }

    public Salle(String nomSalle, int capaciteSalle) {
        this.nom = nomSalle;
        this.capacite = capaciteSalle;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nomSalle) {
        this.nom = nomSalle;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capaciteSalle) {
        this.capacite = capaciteSalle;
    }

    @Override
    public String toString() {
        return "Salle{id=" + id + ", nom='" + nom + "', capacite=" + capacite + "}";
    }
}
