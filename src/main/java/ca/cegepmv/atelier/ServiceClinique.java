package ca.cegepmv.atelier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Atelier — Réusinage (refactoring)
 * =================================
 * <p>
 * Cette classe gère la facturation, la planification et les rapports de la clinique.
 * Elle fonctionne correctement — tous les tests fournis passent — mais elle est
 * volontairement truffée de plusieurs "code smells" du catalogue vu en semaine 6 :
 * <p>
 * - {@link #genererFacture} : méthode trop longue (Long Method) qui mélange
 * validation, calcul et formatage.
 * - {@link #genererFacture}, {@link #estimerCoutAnnuel} et {@link #calculerDureeRendezVous} :
 * aiguillage répété sur {@code visite.getType()} (Repeated Conditionals) — la même
 * logique de tarif de base est dupliquée (Duplicated Code) entre les deux premières.
 * - {@link #planifierVisite} : liste de paramètres trop longue (Long Parameter List).
 * - {@link #genererRapportPourVeterinaire} : s'intéresse presque exclusivement aux
 * données de {@link Proprietaire} plutôt qu'aux siennes (Feature Envy).
 * - {@link #calculerRemiseFidelite} : imbriquement profond de conditions qui gagnerait
 * à utiliser des clauses de garde (Guard Clauses).
 * - Le "data clump" rue/ville/codePostal vit dans {@link Proprietaire}, pas ici, mais
 * {@link #genererRapportPourVeterinaire} en dépend directement.
 * <p>
 * ⚠️ RAPPEL DU COURS : ne réusinez jamais du code non couvert par un test. Certaines
 * méthodes ci-dessous n'ont AUCUN test dans {@code ServiceCliniqueTest} — c'est
 * intentionnel : votre première tâche (voir README, Partie 2) est d'écrire des tests
 * de caractérisation qui documentent leur comportement ACTUEL, avant d'y toucher.
 * <p>
 * ⚠️ PIÈGE INTENTIONNEL : un type de visite inconnu (mal orthographié, par exemple)
 * retombe silencieusement sur un tarif de 0.0$ et une durée de 30 minutes, sans jamais
 * lancer d'exception. Cela ressemble à un bogue — ce n'en est pas un que vous devez
 * corriger cette semaine. Réusiner ne change JAMAIS le comportement observable, même
 * un comportement qui semble discutable.
 */
public class ServiceClinique {

    private final List<String> journalRappels = new ArrayList<>();

    /**
     * Génère le texte de facture pour un propriétaire, à partir de la liste de visites
     * à facturer.
     * <p>
     * Règles métier actuelles (à préserver EXACTEMENT après réusinage) :
     * - Tarif de base : CONSULTATION = 60$, VACCIN = 35$, CHIRURGIE = 250$, tout autre
     * type = 0$ (silencieusement).
     * - Une visite urgente coûte 1.5x son tarif de base.
     * - Un propriétaire avec PLUS D'UN animal (strictement) obtient 10% de rabais sur
     * le sous-total.
     * - Un solde de compte négatif ajoute une pénalité de 5% sur le total (après rabais).
     */
    public String genererFacture(Proprietaire proprietaire, List<Visite> visites) {
        verifieParametresFacture(proprietaire == null || visites == null || visites.isEmpty(), "Le propriétaire et au moins une visite sont requis");

        double sousTotal = getSousTotal(visites);

        double remise = getRemise(proprietaire);
        double total = sousTotal - (sousTotal * remise);

        total = getTotal(proprietaire, total);

        return getStringFacture(proprietaire, sousTotal, remise, total);
    }

    private String getStringFacture(Proprietaire proprietaire, double sousTotal, double remise, double total) {
        StringBuilder sb = new StringBuilder();
        sb.append("Facture pour ").append(proprietaire.getPrenom()).append(" ").append(proprietaire.getNom());
        sb.append(" — Sous-total: $").append(String.format(Locale.US, "%.2f", sousTotal));
        if (remise > 0) {
            sb.append(" — Remise fidélité: -").append((int) Math.round(remise * 100)).append("%");
        }
        sb.append(" — Total: $").append(String.format(Locale.US, "%.2f", total));
        return sb.toString();
    }

    private double getTotal(Proprietaire proprietaire, double total) {
        if (proprietaire.getSolde() < 0) {
            total = total + (total * 0.05);
        }
        return total;
    }

    private double getRemise(Proprietaire proprietaire) {
        double remise = 0.0;
        if (proprietaire.getAnimaux().size() > 1) {
            remise = 0.10;
        }
        return remise;
    }

    private double getSousTotal(List<Visite> visites) {
        double sousTotal = 0.0;
        for (Visite visite : visites) {
            double tarifBase = (new TypeVisite(visite.getType())).getTarifBase();
            double tarif = visite.isUrgence() ? tarifBase * 1.5 : tarifBase;
            sousTotal += tarif;
        }
        return sousTotal;
    }

    private void verifieParametresFacture(boolean proprietaire, String s) {
        if (proprietaire) {
            throw new IllegalArgumentException(s);
        }
    }

    /**
     * Projette un coût annuel approximatif à partir d'un échantillon de visites d'un
     * mois type (on multiplie simplement par 12 — projection volontairement naïve).
     * <p>
     * ⚠️ Aucun test ne couvre cette méthode actuellement.
     */
    public double estimerCoutAnnuel(List<Visite> visites) {
        if (visites == null || visites.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Visite visite : visites) {
            double tarifBase = (new TypeVisite(visite.getType())).getTarifBase();
            total += tarifBase;
        }
        return total * 12;
    }

    /**
     * Retourne la durée estimée (en minutes) d'un rendez-vous selon le type de visite.
     * <p>
     * ⚠️ Aucun test ne couvre cette méthode actuellement.
     */
    public int calculerDureeRendezVous(Visite visite) {
        return (new TypeVisite(visite.getType())).getDuree();
    }

    /**
     * Construit un rapport texte à l'intention d'un vétérinaire, résumant les
     * coordonnées du propriétaire et le nombre de visites.
     * <p>
     * ⚠️ Aucun test ne couvre cette méthode actuellement.
     */
    public String genererRapportPourVeterinaire(Veterinaire veterinaire, Proprietaire proprietaire,
                                                List<Visite> visites) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rapport du Dr ").append(veterinaire.getNom())
                .append(" (").append(veterinaire.getSpecialite()).append(")\n");
        sb.append(proprietaire.resumeContact());
        sb.append("Nombre de visites: ").append(visites.size());
        return sb.toString();
    }

    public Visite planifierVisite(int idAnimal, LocalDate date, String type, boolean urgence,
                                  String nomVeterinaire, String notes, boolean envoyerRappelEmail) {

        return planifierVisiterInternal(new DemandeVisite(idAnimal, date, type, urgence, nomVeterinaire, notes, envoyerRappelEmail));
    }

    ;

    public Visite planifierVisite(DemandeVisite demandeVisite) {
        return planifierVisiterInternal(demandeVisite);
    }

    private Visite planifierVisiterInternal(DemandeVisite demandeVisite) {
        if (demandeVisite.type == null || demandeVisite.type.isBlank()) {
            throw new IllegalArgumentException("Le type de visite est requis");
        }
        Visite visite = new Visite(demandeVisite.date, demandeVisite.type, demandeVisite.urgence, demandeVisite.nomVeterinaire, demandeVisite.notes);
        if (demandeVisite.envoyerRappelEmail) {
            journalRappels.add("Rappel envoyé pour la visite du " + demandeVisite.date + " (animal #" + demandeVisite.idAnimal + ")");
        }
        return visite;
    }

    /**
     * Calcule le taux de remise de fidélité applicable à un propriétaire.
     * <p>
     * Règles métier actuelles (à préserver EXACTEMENT après réusinage) :
     * - Propriétaire null → 0.0
     * - Solde négatif → 0.0 (aucune remise si le compte n'est pas à jour)
     * - Plus d'un animal ET client depuis 3 ans ou plus → 0.15
     * - Plus d'un animal ET client depuis moins de 3 ans → 0.10
     * - Un seul animal ET client depuis 5 ans ou plus → 0.05
     * - Un seul animal ET client depuis moins de 5 ans → 0.0
     */
    public double calculerRemiseFidelite(Proprietaire proprietaire) {
        if (proprietaire == null) {
            return 0.0;
        }
        if (proprietaire.getSolde() < 0) {
            return 0.0;
        }
        if (proprietaire.getAnimaux().size() <= 1){
            if (proprietaire.getAnneesClient() < 5) {
                return 0.0;
            }
            if (proprietaire.getAnneesClient() >= 5){
                return 0.05;
            }
        }
        return proprietaire.getAnneesClient() >= 3 ? 0.15 : 0.10;
    }

    public List<String> getJournalRappels() {
        return journalRappels;
    }

    /**
     * Planifie une nouvelle visite pour un animal.
     * <p>
     * ⚠️ Aucun test ne couvre cette méthode actuellement.
     */
    public record DemandeVisite(int idAnimal, LocalDate date, String type, boolean urgence,
                                String nomVeterinaire, String notes, boolean envoyerRappelEmail) {
    }
}
