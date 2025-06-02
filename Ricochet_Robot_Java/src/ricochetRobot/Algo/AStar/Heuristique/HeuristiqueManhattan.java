package ricochetRobot.Algo.AStar.Heuristique;

import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.ObjetsDuJeu.Objective;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class HeuristiqueManhattan implements Heuristique {
    @Override
    public int calculer(EtatPlateau current, Objective goal, Plateau plateau) {
        return Math.abs(current.getRobots().get(goal.getCouleur()).getX() - goal.getPosX()) +
               Math.abs(current.getRobots().get(goal.getCouleur()).getY() - goal.getPosY());
    }
}
