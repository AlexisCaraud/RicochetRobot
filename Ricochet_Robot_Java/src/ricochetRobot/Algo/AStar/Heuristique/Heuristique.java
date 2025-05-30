package ricochetRobot.Algo.AStar.Heuristique;

import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.ObjetsDuJeu.Objective;
import ricochetRobot.ObjetsDuJeu.Plateau;

@FunctionalInterface
public interface Heuristique {
    int calculer(EtatPlateau current, Objective goal, Plateau plateau);
}

