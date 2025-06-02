package ricochetRobot.Etats.GestionEtat;
import java.util.*;

import ricochetRobot.Etats.EtatPlateau;

public class GestionEtat {
    private Set<EtatPlateau> etatsRencontres;

    public GestionEtat() {
        etatsRencontres = new HashSet<>();
    }

    // Ajouter un nouvel état
    public void ajouterEtat(EtatPlateau etat) {
        etatsRencontres.add(etat);
    }

    // Vérifier si un état a déjà été rencontré
    public boolean estDejaRencontre(EtatPlateau etat) {
        return etatsRencontres.contains(etat);
    }

    // Afficher les états rencontrés (facultatif)
    public void afficherEtats() {
        for (EtatPlateau etat : etatsRencontres) {
            System.out.println(etat);
        }
    }

    public int size() {
        return etatsRencontres.size();
    }
}
