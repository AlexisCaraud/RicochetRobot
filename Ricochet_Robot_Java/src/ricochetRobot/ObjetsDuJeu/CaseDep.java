package ricochetRobot.ObjetsDuJeu;

import java.util.HashMap;
import java.util.Map;

public class CaseDep implements Cloneable {
    private Map<String, Coordonnees> mouvements = new HashMap<>();

    public void setDeplacement(String direction, Coordonnees coord) {
        mouvements.put(direction, coord);
    }

    public Coordonnees getDeplacement(String direction) {
        return mouvements.get(direction);
    }

    public Map<String, Coordonnees> getTousDeplacements() {
        return mouvements;
    }

    @Override
    public String toString() {
        return mouvements.toString();
    }

    @Override
public CaseDep clone() {
    try {
        CaseDep copie = (CaseDep) super.clone();
        copie.mouvements = new HashMap<>();
        for (Map.Entry<String, Coordonnees> entry : this.mouvements.entrySet()) {
            copie.mouvements.put(entry.getKey(), (Coordonnees) entry.getValue().clone());
        }
        return copie;
    } catch (CloneNotSupportedException e) {
        throw new AssertionError(e);
    }
}
}
