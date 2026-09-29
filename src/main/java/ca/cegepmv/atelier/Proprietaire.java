package ca.cegepmv.atelier;

import java.util.ArrayList;
import java.util.List;

;
public class Proprietaire {

    private final String prenom;
    private final String nom;
    private final Address address;
    private final String email;
    private final String telephone;
    private final double solde;
    private final int anneesClient;
    private final List<Animal> animaux = new ArrayList<>();

    public Proprietaire(String prenom, String nom, String rue, String ville, String codePostal,
                         String email, String telephone, double solde, int anneesClient) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.address = new Address(rue,ville,codePostal);
        this.telephone = telephone;
        this.solde = solde;
        this.anneesClient = anneesClient;
    }
    public Proprietaire(String prenom, String nom,Address address, String email, String telephone, double solde, int anneesClient){
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.address = address;
        this.telephone = telephone;
        this.solde = solde;
        this.anneesClient = anneesClient;
    }

    public void ajouterAnimal(Animal animal) {
        animaux.add(animal);
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getRue() {
        return address.rue();
    }

    public String getVille() {
        return address.ville();
    }

    public String getCodePostal() {
        return address.codePostal();
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public double getSolde() {
        return solde;
    }

    public int getAnneesClient() {
        return anneesClient;
    }

    public List<Animal> getAnimaux() {
        return animaux;
    }
    public String resumeContact(){
        return "Client: " + this.getPrenom() + " " + this.getNom() + "\n" +
                "Adresse: " + this.getRue() + ", " + this.getVille() +
                " " + this.getCodePostal() + "\n" +
                "Contact: " + this.getEmail() + " / " + this.getTelephone() +
                "\n";
    }
}
