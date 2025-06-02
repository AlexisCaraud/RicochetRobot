package ricochetRobot.Etats.GestionEtat;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.Etats.EncodeurEtat.EncodeurEtatFlexFast;

public class GestionEtatBigInteger {
    private Set<BigInteger > etatsRencontres;

    public GestionEtatBigInteger() {
        etatsRencontres = new HashSet<>();
    }

    public void ajouterEtat(EtatPlateau etat) {
        BigInteger  code = EncodeurEtatFlexFast.encoderEtatPlateau(etat, etat.getPlateau().getObjective().getCouleur());
        etatsRencontres.add(code);
    }

    public boolean estDejaRencontre(EtatPlateau etat) {
        BigInteger code = EncodeurEtatFlexFast.encoderEtatPlateau(etat, etat.getPlateau().getObjective().getCouleur());
        return etatsRencontres.contains(code);
    }

    public int size() {
        return etatsRencontres.size();
    }
}
