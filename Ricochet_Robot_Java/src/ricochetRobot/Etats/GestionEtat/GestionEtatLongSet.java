package ricochetRobot.Etats.GestionEtat;

import java.util.HashSet;
import java.util.Set;

import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.Etats.EncodeurEtat.EncodeurEtat2;

public class GestionEtatLongSet {
    private Set<Long> etatsRencontres;

    public GestionEtatLongSet() {
        etatsRencontres = new HashSet<>();
    }

    public void ajouterEtat(EtatPlateau etat) {
        long code = EncodeurEtat2.encoderEtatPlateau(etat, etat.getPlateau().getObjective().getCouleur());
        etatsRencontres.add(code);
    }

    public boolean estDejaRencontre(EtatPlateau etat) {
        long code = EncodeurEtat2.encoderEtatPlateau(etat, etat.getPlateau().getObjective().getCouleur());
        return etatsRencontres.contains(code);
    }

    public int size() {
        return etatsRencontres.size();
    }
}
