package ricochetRobot.Algo.AStar.Heuristique;

import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.ObjetsDuJeu.Objective;
import ricochetRobot.ObjetsDuJeu.Plateau;

/**
     * Heuristique sur le nombre de déplacement qu'il faut pour aller de l'état actuel à l'objectif
     * On retourne l'opposé de la longueur du chemin pénaliser les chemins longs
     */

public class HeuristiqueNbrDepObj implements Heuristique {
    @Override
    public int calculer(EtatPlateau current, Objective goal, Plateau plateau) {
        String robot = goal.getCouleur();
        int xRobot = current.getRobots().get(robot).getX();
        int yRobot = current.getRobots().get(robot).getY();
        plateau.replacerRobot(current.getRobots());
        Chemin chemin = plateau.tableauDistanceBFS(xRobot, yRobot, robot)[xRobot][yRobot];
        return - chemin.getLongueur();
    }
}
