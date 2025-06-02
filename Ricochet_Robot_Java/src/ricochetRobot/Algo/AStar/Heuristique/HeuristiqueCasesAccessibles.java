package ricochetRobot.Algo.AStar.Heuristique;

import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.ObjetsDuJeu.Objective;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class HeuristiqueCasesAccessibles implements Heuristique {
    @Override
    public int calculer(EtatPlateau current, Objective goal, Plateau plateau) {
        String robot = goal.getCouleur();
        int caseAccessibles = 0;
        Chemin[][] tabDis = plateau.tableauDistanceBFS(
            current.getRobots().get(robot).getX(),
            current.getRobots().get(robot).getY(),
            robot
        );
        for (Chemin[] row : tabDis) {
            for (Chemin chemin : row) {
                if (chemin.getLongueur() < Integer.MAX_VALUE) {
                    caseAccessibles++;
                }
            }
        }
        return caseAccessibles;
    }
}
