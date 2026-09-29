package ca.cegepmv.atelier;

/**
 * Propriétaire d'un ou plusieurs animaux, cliente ou client de la clinique.
 */
public record Address(String rue, String ville, String codePostal){

    @Override
    public String rue() {
        return rue;
    }

    @Override
    public String ville() {
        return ville;
    }

    @Override
    public String codePostal() {
        return codePostal;
    }
}
