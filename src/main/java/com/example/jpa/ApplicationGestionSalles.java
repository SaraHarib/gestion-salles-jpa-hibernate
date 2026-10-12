package com.example.jpa;

import com.example.jpa.entities.Salle;
import com.example.jpa.entities.Utilisateur;
import com.example.jpa.service.ServiceGestion;
import java.util.List;

public class ApplicationGestionSalles {

    public static void main(String[] arguments) {
        int capaciteMinimum = 30;
        String nomSalleRecherchee = "Salle Horizon";
        List<Salle> sallesCreees;
        List<Utilisateur> utilisateursCrees;
        List<Salle> listeSalles;
        List<Utilisateur> listeUtilisateurs;
        List<Salle> sallesDisponibles;
        List<Utilisateur> utilisateursTrouves;

        // Le service est fermé automatiquement à la fin de la démonstration JPA.
        try (ServiceGestion serviceGestion = new ServiceGestion()) {
            Salle salleHorizon = serviceGestion.ajouterSalle("Salle Horizon", 30);
            Salle sallePalmeraie = serviceGestion.ajouterSalle("Salle Palmeraie", 14);
            Salle salleAndalousie = serviceGestion.ajouterSalle("Salle Andalousie", 60);
            sallesCreees = List.of(salleHorizon, sallePalmeraie, salleAndalousie);

            Utilisateur utilisateurLina = serviceGestion.ajouterUtilisateur(
                    "Lina Amrani", "lina.amrani@example.com", salleHorizon);
            Utilisateur utilisateurMehdi = serviceGestion.ajouterUtilisateur(
                    "Mehdi Berrada", "mehdi.berrada@example.com", salleHorizon);
            Utilisateur utilisateurRania = serviceGestion.ajouterUtilisateur(
                    "Rania Tazi", "rania.tazi@example.com", sallePalmeraie);
            Utilisateur utilisateurSami = serviceGestion.ajouterUtilisateur(
                    "Sami Fassi", "sami.fassi@example.com", salleAndalousie);
            utilisateursCrees = List.of(utilisateurLina, utilisateurMehdi, utilisateurRania, utilisateurSami);

            listeSalles = serviceGestion.listerSalles();
            listeUtilisateurs = serviceGestion.listerUtilisateurs();
            sallesDisponibles = serviceGestion.rechercherSallesParCapacite(capaciteMinimum);
            utilisateursTrouves = serviceGestion.rechercherUtilisateursParSalle(nomSalleRecherchee);
        }

        // Le récapitulatif reste regroupé après les traces SQL et la fermeture d'Hibernate.
        System.out.println();
        System.out.println("============================================================");
        System.out.println("       DÉMONSTRATION JPA - GESTION DES SALLES");
        System.out.println("============================================================");
        afficherSalles("1. CRÉATION DES SALLES", sallesCreees);
        afficherUtilisateurs("2. CRÉATION DES UTILISATEURS", utilisateursCrees);
        afficherSalles("3. LISTE DES SALLES ENREGISTRÉES", listeSalles);
        afficherUtilisateurs("4. LISTE DES UTILISATEURS ENREGISTRÉS", listeUtilisateurs);
        afficherSalles("5. SALLES DE CAPACITÉ SUPÉRIEURE OU ÉGALE À " + capaciteMinimum, sallesDisponibles);
        afficherUtilisateurs("6. UTILISATEURS DE LA SALLE : " + nomSalleRecherchee, utilisateursTrouves);
        System.out.println();
        System.out.println("Démonstration terminée avec succès. Ressources JPA fermées.");
    }

    private static void afficherSalles(String titre, List<Salle> listeSalles) {
        System.out.println();
        System.out.println("--- " + titre + " ---");
        for (Salle salleAffichee : listeSalles) {
            System.out.printf("  [%d] %s | Capacité : %d places%n",
                    salleAffichee.getId(), salleAffichee.getNom(), salleAffichee.getCapacite());
        }
        System.out.println("Total : " + listeSalles.size() + " salle(s).");
    }

    private static void afficherUtilisateurs(String titre, List<Utilisateur> listeUtilisateurs) {
        System.out.println();
        System.out.println("--- " + titre + " ---");
        for (Utilisateur utilisateurAffiche : listeUtilisateurs) {
            String nomSalle = utilisateurAffiche.getSalle() != null
                    ? utilisateurAffiche.getSalle().getNom() : "Aucune salle";
            System.out.printf("  [%d] %s | %s | %s%n", utilisateurAffiche.getId(),
                    utilisateurAffiche.getNom(), utilisateurAffiche.getEmail(), nomSalle);
        }
        System.out.println("Total : " + listeUtilisateurs.size() + " utilisateur(s).");
    }
}
