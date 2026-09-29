package ca.cegepmv.atelier;

public class TypeVisite {


    double tarifBase = 0.0;


    int duree = 0;
    TypeVisite(){};
    TypeVisite(String type){
        switch (type){
            case "CONSULTATION" : {
                tarifBase = 60.0;
                duree = 20;
                break;
            }
            case "VACCIN" : {
                tarifBase = 35.0;
                duree = 10;
                break;
            }
            case "CHIRURGIE" : {
                tarifBase = 250.0;
                duree = 90;
                break;
            }
            default : {
                tarifBase = 0.0;
                duree = 30;
            }
        }
    }
    public double getTarifBase() {
        return tarifBase;
    }
    public int getDuree() {
        return duree;
    }
}
